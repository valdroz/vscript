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
 *  RunBlock interface.
 *
 *  @author Valerijus Dorzdovas 
 */
public interface RunBlock
{
	/**
	 * setMasterRunBlock - set the reference to parent MasterRunBlock object
	 */
	void setMasterRunBlock(MasterRunBlock runScript);
	
	/**
	 * executes the run block
	 */
	void run(VariantContainer variantContainer);

	
}
