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
package nokeebuild.buildcache;

import com.gradle.develocity.agent.gradle.DevelocityConfiguration;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

class BuildCachePlugin implements Plugin<Settings> {
	private final ProviderFactory providers;

	@Inject
	public BuildCachePlugin(ProviderFactory providers) {
		this.providers = providers;
	}

	@Override
	public void apply(Settings settings) {
		final DevelocityConfiguration develocity = settings.getExtensions().getByType(DevelocityConfiguration.class);
		final boolean isCI = providers.environmentVariable("CI").forUseAtConfigurationTime().isPresent();
		final boolean hasAccessKey = providers.environmentVariable("DEVELOCITY_ACCESS_KEY").forUseAtConfigurationTime().isPresent()
			|| providers.environmentVariable("GRADLE_ENTERPRISE_ACCESS_KEY").forUseAtConfigurationTime().isPresent();

		settings.buildCache(buildCache -> {
			buildCache.local(local -> {
				local.setEnabled(true);
				local.setRemoveUnusedEntriesAfterDays(Integer.MAX_VALUE);
			});
			buildCache.remote(develocity.getBuildCache(), remote -> {
				remote.setEnabled(true);
				// Avoid build-cache push errors on PR builds where no access key is present.
				remote.setPush(isCI && hasAccessKey);
			});
		});
	}
}
