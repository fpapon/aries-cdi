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

package org.apache.aries.cdi.container.internal.container;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.reference.BeanServiceObjects;

public class BeanServiceObjectsImpl<T> implements BeanServiceObjects<T> {

	public BeanServiceObjectsImpl(ServiceObjects<T> so) {
		_so = so;
	}

	public void close() {
		_objects.removeIf(
			o -> {
				_so.ungetService(o);
				return true;
			}
		);
	}

	@Override
	public T getService() {
		T service = _so.getService();
		_objects.add(service);
		return service;
	}

	@Override
	public ServiceReference<T> getServiceReference() {
		return _so.getServiceReference();
	}

	@Override
	public void ungetService(T service) {
		_objects.remove(service);
		_so.ungetService(service);
	}

	private final Set<T> _objects = ConcurrentHashMap.newKeySet();
	private final ServiceObjects<T> _so;
}