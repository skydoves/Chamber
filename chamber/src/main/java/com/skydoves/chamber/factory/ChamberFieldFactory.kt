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

import com.skydoves.chamber.ChamberField

/**
 * ChamberFieldFactory is a factory class for
 * creating an instance of [ChamberField].
 */
object ChamberFieldFactory {

  /** creates a new instance of [ChamberField]. */
  fun createNewInstance(
    annotation: Annotation,
    key: String,
    value: Any?,
    autoClear: Boolean
  ): ChamberField<*> {
    val chamberField = ChamberField(value)
    chamberField.annotation = annotation
    chamberField.key = key
    chamberField.autoClear(autoClear)
    chamberField.initialized = true
    return chamberField
  }

  /** initializes properties to a [ChamberField]. */
  fun initializeProperties(
    chamberField: ChamberField<*>,
    annotation: Annotation,
    key: String,
    autoClear: Boolean
  ): ChamberField<*> {
    chamberField.annotation = annotation
    chamberField.key = key
    chamberField.autoClear(autoClear)
    chamberField.initialized = true
    return chamberField
  }
}
