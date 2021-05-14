/*
 * Copyright (C) 2019 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused", "SameParameterValue")

package com.skydoves.chamber

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleOwner
import com.skydoves.chamber.annotation.ChamberScope
import com.skydoves.chamber.annotation.ShareProperty
import com.skydoves.chamber.executor.ArchTaskExecutor
import com.skydoves.chamber.factory.ChamberLifecycleObserverFactory
import com.skydoves.chamber.factory.ChamberPropertyFactory

/**
 * Chamber is scoped data holder with custom scopes that are lifecycle aware.
 *
 * Simplifies sharing fields and communication between
 * Android components with custom scopes that are lifecycle aware.
 *
 * Making easier to communicate and data flow with each other
 * component like Activity, Fragment, Services, etc.
 *
 * And using custom scopes that are lifecycle aware makes
 * developers can designate scoped data holder on their taste.
 */
object Chamber {

  /** internal storage [ChamberStore]. */
  private val chamberStore: ChamberStore = ChamberStore()

  /**
   * Chamber synchronizes the ChamberProperty that has the same scope and same key.
   * Also pushes a lifecycleOwner to the Chamber lifecycle stack.
   */
  @JvmStatic
  inline fun <reified T : LifecycleOwner> shareLifecycle(scopeOwner: Any, lifecycleOwner: T) {
    for (annotation in scopeOwner.javaClass.annotations) {

      if (!checkAnnotatedChamberScope(annotation)) continue

      for (field in scopeOwner.javaClass.declaredFields) {
        if (ChamberProperty::class.java.isAssignableFrom(field.type)) {

          store().initializeFieldScopeMap(annotation)

          val shareProperty = field.getAnnotation(ShareProperty::class.java)
            ?: throw IllegalArgumentException(
              "The Chamber property ${field.name}" +
                " should have a @SharedProperty annotation."
            )

          val key = shareProperty.key
          store().getFieldScopeMap(annotation)?.let {
            if (it.contains(key)) {
              val newChamberProperty =
                ChamberPropertyFactory.createNewInstance(
                  annotation,
                  key,
                  it[key]?.value,
                  shareProperty.autoClear
                )
              lifecycleOwner.lifecycle.addObserver(newChamberProperty)
              field.isAccessible = true
              field.set(scopeOwner, newChamberProperty)
              it[key] = newChamberProperty
            } else {
              val declaredField = field.get(scopeOwner) as ChamberProperty<*>
              lifecycleOwner.lifecycle.addObserver(declaredField)
              it[key] =
                ChamberPropertyFactory.initializeProperties(
                  declaredField,
                  annotation,
                  key,
                  shareProperty.autoClear
                )
            }
          }

          store().initializeObserverScopeStack(annotation)

          val observer = ChamberLifecycleObserverFactory.createNewInstance(annotation, lifecycleOwner)
          if (!store().checkContainsChamberLifecycleObserver(annotation, lifecycleOwner.toString())) {
            lifecycleOwner.lifecycle.addObserver(observer)
            store().getLifecycleObserverStack(annotation)?.push(observer)
          }
        }
      }
    }
  }

  /** gets internal storage [ChamberStore]. */
  @JvmStatic
  @PublishedApi
  internal fun store(): ChamberStore {
    return this.chamberStore
  }

  /** updates a new [ChamberProperty] to the caches. */
  @JvmStatic
  @MainThread
  fun updateValue(chamberProperty: ChamberProperty<*>) {
    assertMainThread("updateValue")
    store().getFieldScopeMap(chamberProperty.annotation)?.let {
      it.remove(chamberProperty.key)
      it.put(chamberProperty.key, chamberProperty)
    }
  }

  /** clears value data and observer on internal storage. */
  @JvmStatic
  fun onDestroyObserver(annotation: Annotation) {
    store().getLifecycleObserverStack(annotation)?.pop()
    if (store().getLifecycleObserverStackSize(annotation) == 0) {
      store().clearFieldScope(annotation)
      store().clearLifecycleObserverScope(annotation)
    }
  }

  /** checks the ScopeOwner class is annotated @ChamberScope annotation. */
  @JvmStatic
  @VisibleForTesting
  fun checkAnnotatedChamberScope(annotation: Annotation): Boolean {
    return annotation.annotationClass.annotations.toString()
      .contains(ChamberScope::class.java.name)
  }

  /** asserts the method is invoked on the main thread. */
  private fun assertMainThread(methodName: String) {
    ArchTaskExecutor.instance?.let {
      check(it.isMainThread) {
        "Cannot invoke " + methodName + " on a background" +
          " thread"
      }
    }
  }

  /** clears all of [ChamberProperty] hash caches & lifecycle stacks. */
  @JvmStatic
  fun destroyStore() {
    store().clear()
  }
}
