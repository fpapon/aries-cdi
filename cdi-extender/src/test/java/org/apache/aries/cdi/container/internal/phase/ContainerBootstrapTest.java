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

package org.apache.aries.cdi.container.internal.phase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.aries.cdi.container.internal.container.CheckedCallback;
import org.apache.aries.cdi.container.internal.container.ConfigurationListener;
import org.apache.aries.cdi.container.internal.container.ContainerBootstrap;
import org.apache.aries.cdi.container.internal.container.ContainerState;
import org.apache.aries.cdi.container.internal.container.Op;
import org.apache.aries.cdi.container.internal.model.ContainerActivator;
import org.apache.aries.cdi.container.internal.model.ExtendedComponentInstanceDTO;
import org.apache.aries.cdi.container.internal.model.FactoryComponent;
import org.apache.aries.cdi.container.internal.model.SingleComponent;
import org.apache.aries.cdi.container.internal.util.Logs;
import org.apache.aries.cdi.container.test.BaseCDIBundleTest;
import org.apache.aries.cdi.container.test.TestUtil;
import org.apache.aries.cdi.container.test.beans.FooService;
import org.apache.aries.cdi.spi.CDIContainerInitializer;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cdi.CDIConstants;
import org.osgi.service.cdi.runtime.dto.ComponentDTO;
import org.osgi.service.cdi.runtime.dto.ContainerDTO;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class ContainerBootstrapTest extends BaseCDIBundleTest {

	@Ignore
	@Test
	public void test_publishServices() throws Exception {
		Map<String, Object> attributes = new HashMap<>();

		attributes.put(
			CDIConstants.REQUIREMENT_BEANS_ATTRIBUTE,
			Arrays.asList(
				FooService.class.getName()
			)
		);

		when(
			bundle.adapt(
				BundleWiring.class).getRequiredWires(
					ExtenderNamespace.EXTENDER_NAMESPACE).get(
						0).getRequirement().getAttributes()
		).thenReturn(attributes);

		ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> caTracker = TestUtil.mockCaSt(bundle);

		ContainerState containerState = new ContainerState(bundle, ccrBundle, ccrChangeCount, promiseFactory, caTracker, new Logs.Builder(bundle.getBundleContext()).build());

		ComponentDTO componentDTO = new ComponentDTO();
		componentDTO.instances = new CopyOnWriteArrayList<>();
		componentDTO.template = containerState.containerDTO().template.components.get(0);

		@SuppressWarnings("unchecked")
		ServiceTracker<CDIContainerInitializer, ServiceObjects<CDIContainerInitializer>> serviceTracker = mock(ServiceTracker.class);

		@SuppressWarnings("unchecked")
		ContainerBootstrap containerBootstrap = new ContainerBootstrap(
			containerState, serviceTracker,
			new ConfigurationListener.Builder(containerState),
			new SingleComponent.Builder(containerState, null),
			new FactoryComponent.Builder(containerState, null),
			mock(ServiceTracker.class));

		ExtendedComponentInstanceDTO componentInstanceDTO = new ExtendedComponentInstanceDTO(containerState, new ContainerActivator.Builder(containerState, containerBootstrap));
		componentInstanceDTO.activations = new CopyOnWriteArrayList<>();
		componentInstanceDTO.configurations = new CopyOnWriteArrayList<>();
		//componentInstanceDTO.pid = componentDTO.template.configurations.get(0).pid;
		componentInstanceDTO.properties = null;
		componentInstanceDTO.references = new CopyOnWriteArrayList<>();
		componentInstanceDTO.template = componentDTO.template;

		componentDTO.instances.add(componentInstanceDTO);

		containerState.containerDTO().components.add(componentDTO);

		Promise<Boolean> p0 = containerState.addCallback(
			(CheckedCallback<Boolean, Boolean>) op -> {
				return op.mode == Op.Mode.OPEN && op.type == Op.Type.CONTAINER_INIT_COMPONENTS;
			}
		);

		containerBootstrap.open();

		ContainerDTO containerDTO = containerState.containerDTO();
		assertNotNull(containerDTO);
		assertEquals(1, containerDTO.changeCount);
		assertTrue(containerDTO.errors + "", containerDTO.errors.isEmpty());
		assertNotNull(containerDTO.template);

		assertTrue(p0.timeout(200).getValue());
	}

}
