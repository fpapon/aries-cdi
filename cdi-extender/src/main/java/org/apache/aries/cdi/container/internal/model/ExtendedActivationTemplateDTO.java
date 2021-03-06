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

package org.apache.aries.cdi.container.internal.model;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.AnnotatedMember;

import org.osgi.service.cdi.runtime.dto.template.ActivationTemplateDTO;

public class ExtendedActivationTemplateDTO extends ActivationTemplateDTO {

	/**
	 * The class which declared the activation (i.e. @Service).
	 */
	public Class<?> declaringClass;

	public Class<? extends Annotation> cdiScope;

	public AnnotatedMember<?> producer;

}
