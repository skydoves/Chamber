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
import androidx.lifecycle.LifecycleOwner
import com.skydoves.chamber.annotation.ChamberScope
import com.skydoves.chamber.annotation.PropertyObserver
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

    val annotation = scopeOwner.javaClass.annotations.filter { checkAnnotatedChamberScope(it) }
      .takeIf { !it.isNullOrEmpty() }?.first()
      ?: throw RuntimeException(
        "The scope owner $scopeOwner must be annotated " +
          "with a custom scope that has a @ChamberScope annotation."
      )

    scopeOwner.javaClass.declaredFields
      .filter { ChamberProperty::class.java.isAssignableFrom(it.type) }
      .forEach { field ->

        store().initializeFieldScopeMap(annotation)

        field.isAccessible = true

        val shareProperty = field.getAnnotation(ShareProperty::class.java)
          ?: throw IllegalArgumentException(
            "The Chamber property ${field.name}" +
              " should have a @ShareProperty annotation."
          )

        val key = shareProperty.key
        val observerMethods = scopeOwner.javaClass.declaredMethods
          .filter { method ->
            val propertyObserver = method.getAnnotation(PropertyObserver::class.java)
            propertyObserver != null && propertyObserver.key == key && method.parameterTypes.size == 1
          }

        store().getFieldScopeMap(annotation)?.let {
          if (it.contains(key)) {
            val newChamberProperty =
              ChamberPropertyFactory.createNewInstance(
                annotation = annotation,
                key = key,
                value = it[key]?.value,
                clearOnDestroy = shareProperty.clearOnDestroy,
                scopeOwner = scopeOwner,
                observerMethods = observerMethods
              )
            lifecycleOwner.lifecycle.addObserver(newChamberProperty)
            field.set(scopeOwner, newChamberProperty)
            it[key] = newChamberProperty
          } else {
            val declaredField = field.get(scopeOwner) as ChamberProperty<*>
            lifecycleOwner.lifecycle.addObserver(declaredField)
            it[key] =
              ChamberPropertyFactory.initializeProperties(
                chamberProperty = declaredField,
                annotation = annotation,
                key = key,
                clearOnDestroy = shareProperty.clearOnDestroy,
                scopeOwner = scopeOwner,
                observerMethods = observerMethods
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

  /** Returns an internal storage [ChamberStore]. */
  @JvmStatic
  @PublishedApi
  internal fun store(): ChamberStore {
    return this.chamberStore
  }

  /** Updates a new [ChamberProperty] information to the [ChamberStore] caches. */
  @JvmStatic
  @MainThread
  fun updateValue(chamberProperty: ChamberProperty<*>) {
    assertMainThread("updateValue")
    store().getFieldScopeMap(chamberProperty.annotation)?.let {
      it.remove(chamberProperty.key)
      it.put(chamberProperty.key, chamberProperty)
    }
  }

  /** Clears [ChamberProperty] data and observer in the [ChamberStore] caches. */
  @JvmStatic
  fun onDestroyObserver(annotation: Annotation) {
    with(store()) {
      getLifecycleObserverStack(annotation)?.pop()
      if (getLifecycleObserverStackSize(annotation) == 0) {
        clearFieldScope(annotation)
        clearLifecycleObserverScope(annotation)
      }
    }
  }

  /** Checks the ScopeOwner class is annotated with [@ChamberScope] annotation. */
  @JvmStatic
  @PublishedApi
  internal fun checkAnnotatedChamberScope(annotation: Annotation): Boolean {
    return annotation.annotationClass.annotations.toString()
      .contains(ChamberScope::class.java.name)
  }

  /** Asserts the method is invoked on the main thread. */
  private fun assertMainThread(methodName: String) {
    ArchTaskExecutor.instance?.let {
      check(it.isMainThread) {
        "Cannot invoke " + methodName + " on a background" +
          " thread"
      }
    }
  }

  /** Clears all of [ChamberProperty] hash caches and lifecycle stacks. */
  @JvmStatic
  fun destroyStore() {
    store().clear()
  }
}
