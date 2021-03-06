/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.aries.cdi.owb;

import org.apache.webbeans.spi.ApplicationBoundaryService;

public class OsgiApplicationBoundaryService implements ApplicationBoundaryService {
	private final ClassLoader bundleLoader;
	private final ClassLoader loader;

	public OsgiApplicationBoundaryService(final ClassLoader bundleLoader, final ClassLoader loader) {
		this.bundleLoader = bundleLoader;
		this.loader = loader;
	}

	@Override
	public ClassLoader getApplicationClassLoader() {
		return bundleLoader;
	}

	@Override
	public ClassLoader getBoundaryClassLoader(@SuppressWarnings("rawtypes") final Class aClass) {
		final ClassLoader classToProxyCl = aClass.getClassLoader();
		if (classToProxyCl == null || classToProxyCl == loader) {
			return loader;
		}
		if (classToProxyCl == bundleLoader) {
			return classToProxyCl;
		}
		// todo: refine if needed
		return classToProxyCl;
	}
}
