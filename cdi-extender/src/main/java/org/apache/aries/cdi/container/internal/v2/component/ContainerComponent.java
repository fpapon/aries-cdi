package org.apache.aries.cdi.container.internal.v2.component;

import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.cdi.runtime.dto.ComponentDTO;
import org.osgi.service.cdi.runtime.dto.template.ComponentTemplateDTO;
import org.osgi.service.cdi.runtime.dto.template.ConfigurationPolicy;
import org.osgi.service.cdi.runtime.dto.template.ConfigurationTemplateDTO;
import org.osgi.service.cdi.runtime.dto.template.MaximumCardinality;
import org.osgi.service.cdi.runtime.dto.template.ReferenceTemplateDTO;

public class ContainerComponent implements Component {

	public ContainerComponent(String containerId) {
		_snapshot = new ComponentDTO();
		_snapshot.template = new ComponentTemplateDTO();
		_snapshot.template.activations = new CopyOnWriteArrayList<>();
		_snapshot.template.configurations = new CopyOnWriteArrayList<>();

		ConfigurationTemplateDTO factoryConfig = new ConfigurationTemplateDTO();
		factoryConfig.componentConfiguration = true;
		factoryConfig.maximumCardinality = MaximumCardinality.ONE;
		factoryConfig.pid = containerId;
		factoryConfig.policy = ConfigurationPolicy.OPTIONAL;

		_snapshot.template.configurations.add(factoryConfig);
		_snapshot.template.name = containerId;
		_snapshot.template.references = new CopyOnWriteArrayList<>();
		_snapshot.template.type = ComponentTemplateDTO.Type.CONTAINER;
	}

	@Override
	public void addConfiguration(ConfigurationTemplateDTO dto) {
		if (dto == null) return;
		_snapshot.template.configurations.add(dto);
	}

	@Override
	public void addReference(ReferenceTemplateDTO dto) {
		if (dto == null) return;
		_snapshot.template.references.add(dto);
	}

	@Override
	public ComponentDTO getSnapshot() {
		return _snapshot; // TODO make safe copy using converter
	}

	@Override
	public ComponentTemplateDTO getTemplate() {
		return _snapshot.template; // TODO make safe copy using converter
	}

	private final ComponentDTO _snapshot;

}