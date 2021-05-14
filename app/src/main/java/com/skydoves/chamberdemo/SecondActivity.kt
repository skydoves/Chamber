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

package com.skydoves.chamberdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.chamber.annotation.ShareProperty
import com.skydoves.chamber.chamberProperty
import com.skydoves.chamber.shareLifecycle
import com.skydoves.chamberdemo.scope.UserScope
import kotlinx.android.synthetic.main.activity_second.*

@UserScope
class SecondActivity : AppCompatActivity() {

  @ShareProperty(key = "nickname")
  private var username = chamberProperty("skydoves")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_second)

    shareLifecycle()
    username.value = "skydoves on SecondActivity"
    username.observe { Log.e("Test", "data is changed! : $it") }

    Log.e("Test", username.value)

    button.setOnClickListener {
      startActivity(Intent(this, ThirdActivity::class.java))
    }
  }
}
