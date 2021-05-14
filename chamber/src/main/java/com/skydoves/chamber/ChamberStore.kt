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

package com.skydoves.chamber

import java.util.Stack
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/** ChamberStore is an internal storage to store [ChamberProperty] and [ChamberLifecycleObserver]. */
@Suppress("unused")
class ChamberStore {

  private val caches: MutableMap<Annotation, MutableMap<String, ChamberProperty<*>>> = ConcurrentHashMap()
  private val observers: MutableMap<Annotation, Stack<ChamberLifecycleObserver>> = ConcurrentHashMap()

  /** initializes the [ChamberProperty] hash map by a scope. */
  fun initializeFieldScopeMap(annotation: Annotation) = synchronized(this) {
    if (!this.caches.containsKey(annotation)) {
      this.caches[annotation] = ConcurrentHashMap()
    }
  }

  /** initializes the [ChamberLifecycleObserver] stack by a scope. */
  fun initializeObserverScopeStack(annotation: Annotation) = synchronized(this) {
    if (!this.observers.containsKey(annotation)) {
      this.observers[annotation] = Stack()
    }
  }

  /** gets [ChamberProperty] hash map by a scope. */
  fun getFieldScopeMap(annotation: Annotation): MutableMap<String, ChamberProperty<*>>? = synchronized(this) {
    return caches[annotation]
  }

  /** gets [ChamberLifecycleObserver] stack by a scope. */
  fun getLifecycleObserverStack(annotation: Annotation): Stack<ChamberLifecycleObserver>? = synchronized(this) {
    return this.observers[annotation]
  }

  /** checks a [ChamberLifecycleObserver] is already cached or not. */
  fun checkContainsChamberLifecycleObserver(annotation: Annotation, lifecycleOwner: String): Boolean = synchronized(this) {
    var contained = false
    this.observers[annotation]?.let {
      for (observer in it) {
        if (observer.lifecycleOwner == lifecycleOwner) {
          contained = true
          break
        }
      }
    }
    return contained
  }

  /** gets the scoped caching size. */
  fun getFieldScopeCacheSize(): Int = synchronized(this) {
    return caches.size
  }

  /** gets the stack size of [ChamberLifecycleObserver] by a scope. */
  fun getLifecycleObserverStackSize(annotation: Annotation): Int = synchronized(this) {
    return this.observers[annotation]?.size ?: 0
  }

  /** clears a field on the scope cache storage. */
  fun clearField(annotation: Annotation, key: String) = synchronized(this) {
    this.caches[annotation]?.remove(key)
  }

  /** clears a value hash map caches by a scope. */
  fun clearFieldScope(annotation: Annotation) = synchronized(this) {
    this.caches.remove(annotation)
  }

  /** clears a lifecycle stack caches by a scope. */
  fun clearLifecycleObserverScope(annotation: Annotation) = synchronized(this) {
    this.observers.remove(annotation)
  }

  /** clears internal storage. */
  fun clear() = synchronized(this) {
    for (chamberPropertyMap in this.caches.values) {
      chamberPropertyMap.clear()
    }
    this.caches.clear()

    for (chamberObserverMap in this.observers.values) {
      chamberObserverMap.clear()
    }
    this.observers.clear()
  }
}
