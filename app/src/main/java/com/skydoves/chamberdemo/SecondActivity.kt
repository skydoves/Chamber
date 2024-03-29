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
import com.skydoves.chamber.annotation.PropertyObserver
import com.skydoves.chamber.annotation.ShareProperty
import com.skydoves.chamber.chamberProperty
import com.skydoves.chamber.shareLifecycle
import com.skydoves.chamberdemo.databinding.ActivitySecondBinding
import com.skydoves.chamberdemo.scope.UserScope

@UserScope
class SecondActivity : AppCompatActivity() {

  @ShareProperty(key = UserScope.nickname)
  private var username = chamberProperty("skydoves")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivitySecondBinding.inflate(layoutInflater)
    setContentView(binding.root)

    shareLifecycle()

    username.observe { Log.d("SecondActivity", "observed data: $it") }

    username.value = "skydoves on SecondActivity"

    binding.button.setOnClickListener {
      startActivity(Intent(this, ThirdActivity::class.java))
    }
  }

  @PropertyObserver(key = UserScope.nickname)
  fun secondActivityNickNameObserver(nickname: String) {
    Log.d("SecondActivity", "secondActivityNickNameObserver: $nickname")
  }
}
