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

import com.skydoves.chamber.ChamberProperty
import java.lang.reflect.Method

/**
 * ChamberPropertyFactory is a factory class for
 * creating an instance of [ChamberProperty].
 */
@PublishedApi
internal object ChamberPropertyFactory {

  /** creates a new instance of [ChamberProperty]. */
  @JvmSynthetic
  fun createNewInstance(
    annotation: Annotation,
    key: String,
    value: Any?,
    clearOnDestroy: Boolean,
    scopeOwner: Any,
    observerMethods: List<Method>? = null
  ): ChamberProperty<*> = ChamberProperty(value).apply {
    this.annotation = annotation
    this.key = key
    this.clearOnDestroy(clearOnDestroy)
    this.initialized = true
    this.scopeOwner = scopeOwner
    this.observerMethods = observerMethods
  }

  /** initializes properties to a [ChamberProperty]. */
  @JvmSynthetic
  fun initializeProperties(
    chamberProperty: ChamberProperty<*>,
    annotation: Annotation,
    key: String,
    clearOnDestroy: Boolean,
    scopeOwner: Any,
    observerMethods: List<Method>? = null
  ): ChamberProperty<*> = chamberProperty.apply {
    this.annotation = annotation
    this.key = key
    this.clearOnDestroy(clearOnDestroy)
    this.initialized = true
    this.scopeOwner = scopeOwner
    this.observerMethods = observerMethods
  }
}
