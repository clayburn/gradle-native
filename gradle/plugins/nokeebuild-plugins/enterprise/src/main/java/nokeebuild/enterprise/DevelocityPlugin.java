/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nokeebuild.enterprise;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;

class DevelocityPlugin implements Plugin<Settings> {
	@Override
	public void apply(Settings settings) {
		// ORDERING IS IMPORTANT: Develocity is applied first so that
		// nokeebuild.build-cache can read the DevelocityConfiguration extension
		// when wiring the remote cache to develocity.buildCache. CCUD is
		// applied next so that its custom values land on every Build Scan.
		settings.getPluginManager().apply("com.gradle.develocity");
		settings.getPluginManager().apply("com.gradle.common-custom-user-data-gradle-plugin");
		settings.getPluginManager().apply("nokeebuild.build-scan");
		settings.getPluginManager().apply("nokeebuild.build-cache");
	}
}
