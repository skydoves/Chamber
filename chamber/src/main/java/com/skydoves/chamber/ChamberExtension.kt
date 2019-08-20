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

import androidx.lifecycle.LifecycleOwner

/** an extension to invoke [Chamber.shareLifecycle]. */
fun Any.shareLifecycle(lifecycleOwner: LifecycleOwner) {
  Chamber.shareLifecycle(scopeOwner = this, lifecycleOwner = lifecycleOwner)
}

/** an extension to invoke [Chamber.shareLifecycle]. */
fun LifecycleOwner.shareLifecycle() {
  Chamber.shareLifecycle(scopeOwner = this, lifecycleOwner = this)
}

/** creates an instance of [ChamberField]. */
fun <T> chamberField(value: T): ChamberField<T> {
  return ChamberField(value)
}
