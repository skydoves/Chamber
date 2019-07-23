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
import kotlin.collections.set

/** ChamberStore is an internal storage to store [ChamberField] and [ChamberLifecycleObserver]. */
@Suppress("unused")
class ChamberStore {

  private val caches: HashMap<Annotation, HashMap<String, ChamberField<*>>> = HashMap()
  private val observers: HashMap<Annotation, Stack<ChamberLifecycleObserver>> = HashMap()

  /** initializes the [ChamberField] hash map by a scope. */
  fun initializeFieldScopeMap(annotation: Annotation) {
    if (!caches.containsKey(annotation)) {
      caches[annotation] = HashMap()
    }
  }

  /** initializes the [ChamberLifecycleObserver] stack by a scope. */
  fun initializeObserverScopeStack(annotation: Annotation) {
    if (!observers.containsKey(annotation)) {
      observers[annotation] = Stack()
    }
  }

  /** gets [ChamberField] hash map by a scope. */
  fun getFieldScopeMap(annotation: Annotation): HashMap<String, ChamberField<*>>? {
    return caches[annotation]
  }

  /** gets [ChamberLifecycleObserver] stack by a scope. */
  fun getLifecycleObserverStack(annotation: Annotation): Stack<ChamberLifecycleObserver>? {
    return observers[annotation]
  }

  /** checks a [ChamberLifecycleObserver] is already cached or not. */
  fun checkContainsChamberLifecycleObserver(annotation: Annotation, lifecycleOwner: String): Boolean {
    var contained = false
    observers[annotation]?.let {
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
  fun getFieldScopeCacheSize(): Int {
    return caches.size
  }

  /** gets the stack size of [ChamberLifecycleObserver] by a scope. */
  fun getLifecycleObserverStackSize(annotation: Annotation): Int {
    return observers[annotation]?.size ?: 0
  }

  /** clears a value hash map caches by a scope. */
  fun clearFieldScope(annotation: Annotation) {
    caches.remove(annotation)
  }

  /** clears a lifecycle stack caches by a scope. */
  fun clearLifecycleObserverScope(annotation: Annotation) {
    observers.remove(annotation)
  }

  /** clears internal storage. */
  fun clear() {
    for (chamberFieldMap in caches.values) {
      chamberFieldMap.clear()
    }
    caches.clear()

    for (chamberObserverMap in observers.values) {
      chamberObserverMap.clear()
    }
    observers.clear()
  }
}
