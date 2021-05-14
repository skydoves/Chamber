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
@file:JvmName("ChamberExt")
@file:JvmMultifileClass

package com.skydoves.chamber

import androidx.lifecycle.LifecycleOwner

/** An extension to invoke [Chamber.shareLifecycle]. */
fun Any.shareLifecycle(lifecycleOwner: LifecycleOwner) {
  Chamber.shareLifecycle(scopeOwner = this, lifecycleOwner = lifecycleOwner)
}

/** An extension to invoke [Chamber.shareLifecycle]. */
fun LifecycleOwner.shareLifecycle() {
  Chamber.shareLifecycle(scopeOwner = this, lifecycleOwner = this)
}

/** Creates an instance of [ChamberProperty]. */
fun <T> chamberProperty(value: T): ChamberProperty<T> {
  return ChamberProperty(value)
}

/** Creates an instance of [ChamberProperty] using a lambda. */
inline fun <T> chamberProperty(block: () -> T): ChamberProperty<T> {
  return ChamberProperty(block())
}

/** Creates an instance of the [ChamberProperty] from an object. */
fun <T> T.asChamberProperty(): ChamberProperty<T> {
  return ChamberProperty(this)
}
