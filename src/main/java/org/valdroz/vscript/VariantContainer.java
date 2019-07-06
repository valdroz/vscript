/*
 * Copyright 2000 Valerijus Drozdovas
 *
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
package org.valdroz.vscript;

/**
 * Variable container interface.
 * @author Valerijus Drozdovas
 */
public interface VariantContainer
{
	/**
	 * Must set and retain variable value.
	 */
	void setVariant(String varName, Variant varValue);

	/**
	 * Must return current variable value.
	 */
	Variant getVariant(String varName);

	/**
	 * Must be set and retain of an array item.
	 */
	void setVariant(String varName, int index, Variant varValue);

	/**
	 * Must return current value of an array item.
	 */
	Variant getVariant(String varName, int index);

	/**
	 * Must return true if container owns variable.
	 */
	boolean contains(String varName);
}
