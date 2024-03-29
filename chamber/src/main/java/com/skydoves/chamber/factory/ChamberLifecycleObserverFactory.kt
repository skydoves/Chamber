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

package com.skydoves.chamber.factory

import androidx.lifecycle.LifecycleOwner
import com.skydoves.chamber.ChamberLifecycleObserver

/**
 * ChamberLifecycleObserverFactory is a factory class
 * for creating an instance of [ChamberLifecycleObserver].
 */
@PublishedApi
internal object ChamberLifecycleObserverFactory {

  /** Creates a new instance of [ChamberLifecycleObserver]. */
  @JvmSynthetic
  fun createNewInstance(
    annotation: Annotation,
    lifecycleOwner: LifecycleOwner
  ): ChamberLifecycleObserver {
    return ChamberLifecycleObserver(annotation, lifecycleOwner.toString())
  }
}
