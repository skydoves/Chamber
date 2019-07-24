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

/** ChamberLifecycleObserver is an onDestroy observer class. */
class ChamberLifecycleObserver(
  private val annotation: Annotation,
  val lifecycleOwner: String
) : LifecycleObserver {

  /**
   * when lifecycle owner is destroyed, the value data
   * will be cleared on the lifecycle stack on the [ChamberStore].
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    Chamber.onDestroyObserver(annotation)
  }
}
