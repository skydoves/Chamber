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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.skydoves.chamber.executor.ArchTaskExecutor
import kotlin.properties.Delegates

/**
 * ChamberField is an interactive class to the internal Chamber data holder ([ChamberStore])
 * and a lifecycleObserver that can be observable.
 * It should be used with [com.skydoves.chamber.annotation.ShareProperty] annotation
 * that has a key name.
 *
 * If we want to use the same synchronized value on the same custom scope
 * and different classes, we should use the same key.
 */
class ChamberField<T> constructor(value: T) : LifecycleObserver {

  // flag to validate that properties are initialized.
  var initialized: Boolean = false
  // key value for separating each cache data.
  lateinit var key: String
  // annotation value for distinguishing scope.
  lateinit var annotation: Annotation
  // notify the observer when the value value is changed.
  private var observer: ChamberFieldObserver<T>? = null
  // clear the field data from the scoped cache storage when lifecycleOwner state is onDestroy.
  private var autoClear: Boolean = false
  // when the value value is changed, the old value what on the internal storage
  // will be changed as the new value.
  var value: T by Delegates.observable(value) { _, _, _ ->
    run {
      if (initialized) {
        Chamber.updateValue(this)
        observer?.onChanged(this.value)
      }
    }
  }

  private val lock = Any()
  private val empty = Any()
  // when postValue is called, we set the pending data and actual data swap happens on the main
  // thread
  @Volatile
  internal var pending = empty
  @Suppress("UNCHECKED_CAST")
  private val mPostValueRunnable = Runnable {
    synchronized(lock) {
      this.value = pending as T
      pending = empty
      Chamber.updateValue(this)
    }
  }

  /** sets value on the worker thread and post the value to the main thread. */
  fun postValue(value: T) {
    val postTask: Boolean
    synchronized(lock) {
      postTask = pending === empty
      pending = value as Any
    }
    if (!postTask) {
      return
    }
    ArchTaskExecutor.instance?.postToMainThread(mPostValueRunnable)
  }

  /** sets [ChamberFieldObserver] to observe value data change. */
  fun observe(observer: ChamberFieldObserver<T>) {
    this.observer = observer
  }

  /** sets [ChamberFieldObserver] to observe value data change using block. */
  fun observe(block: (t: T?) -> Unit) {
    this.observer = object : ChamberFieldObserver<T> {
      override fun onChanged(t: T) = block(t)
    }
  }

  /** sets auto clear value for clear field automatically when lifecycle state is onDestroy.  */
  fun autoClear(autoClear: Boolean) {
    this.autoClear = autoClear
  }

  /** when lifecycle state is onResume, the value will be updated from local storage. */
  @Suppress("UNCHECKED_CAST")
  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun onResume() {
    value = Chamber.store().getFieldScopeMap(annotation)?.get(key)?.value as T
  }

  /** when lifecycle state is onDestroy, the value will be cleared on the local storage. */
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    if (autoClear) {
      Chamber.store().clearField(annotation, key)
    }
  }
}
