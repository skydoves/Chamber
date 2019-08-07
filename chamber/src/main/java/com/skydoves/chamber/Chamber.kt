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

@file:Suppress("unused")

package com.skydoves.chamber

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleOwner
import com.skydoves.chamber.annotation.ChamberScope
import com.skydoves.chamber.annotation.ShareProperty
import com.skydoves.chamber.executor.ArchTaskExecutor
import com.skydoves.chamber.factory.ChamberFieldFactory
import com.skydoves.chamber.factory.ChamberLifecycleObserverFactory

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
  private val chamberStore = ChamberStore()

  /**
   * Chamber synchronizes the ChamberField that has the same scope and same key.
   * Also pushes a lifecycleOwner to the Chamber lifecycle stack.
   */
  inline fun <reified T : LifecycleOwner> shareLifecycle(scopeOwner: Any, lifecycleOwner: T) {
    for (annotation in scopeOwner.javaClass.annotations) {

      if (!checkAnnotatedChamberScope(annotation)) continue

      for (field in scopeOwner.javaClass.declaredFields) {
        if (ChamberField::class.java.isAssignableFrom(field.type)) {

          store().initializeFieldScopeMap(annotation)

          val shareProperty = field.getAnnotation(ShareProperty::class.java)
            ?: throw IllegalArgumentException("The Chamber property ${field.name}" +
              " should have a @SharedProperty annotation.")

          val key = shareProperty.value
          store().getFieldScopeMap(annotation)?.let {
            if (it.contains(key)) {
              val newChamberField =
                ChamberFieldFactory.createNewInstance(
                  annotation,
                  key,
                  it[key]?.value,
                  shareProperty.autoClear)
              lifecycleOwner.lifecycle.addObserver(newChamberField)
              field.isAccessible = true
              field.set(scopeOwner, newChamberField)
              it[key] = newChamberField
            } else {
              val declaredField = field.get(scopeOwner) as ChamberField<*>
              lifecycleOwner.lifecycle.addObserver(declaredField)
              it[key] =
                ChamberFieldFactory.initializeProperties(
                  declaredField,
                  annotation,
                  key,
                  shareProperty.autoClear)
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
  fun store(): ChamberStore {
    return this.chamberStore
  }

  /** updates a new [ChamberField] to the caches. */
  @MainThread
  fun updateValue(chamberField: ChamberField<*>) {
    assertMainThread("updateValue")
    store().getFieldScopeMap(chamberField.annotation)?.let {
      it.remove(chamberField.key)
      it.put(chamberField.key, chamberField)
    }
  }

  /** clears value data and observer on internal storage. */
  fun onDestroyObserver(annotation: Annotation) {
    store().getLifecycleObserverStack(annotation)?.pop()
    if (store().getLifecycleObserverStackSize(annotation) == 0) {
      store().clearFieldScope(annotation)
      store().clearLifecycleObserverScope(annotation)
    }
  }

  /** checks the ScopeOwner class is annotated @ChamberScope annotation. */
  @VisibleForTesting
  fun checkAnnotatedChamberScope(annotation: Annotation): Boolean {
    return annotation.annotationClass.annotations.toString()
      .contains(ChamberScope::class.java.name)
  }

  /** asserts the method is invoked on the main thread. */
  private fun assertMainThread(methodName: String) {
    ArchTaskExecutor.instance?.let {
      if (!it.isMainThread) {
        throw IllegalStateException("Cannot invoke " + methodName + " on a background" +
          " thread")
      }
    }
  }

  /** clears all of [ChamberField] hash caches & lifecycle stacks. */
  fun destroyStore() {
    store().clear()
  }
}
