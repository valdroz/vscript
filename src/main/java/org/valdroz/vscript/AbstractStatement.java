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
 * Abstract class AbstractStatement. All statement like "if", "while" must be derived
 * from this class.
 *
 * @author Valerijus Drozdovas
 */
public abstract class AbstractStatement<T> implements RunBlock {

    private MasterRunBlock master;
    private RunBlock body;

    public AbstractStatement() {
    }

    public abstract T getThis();

    @Override
    public void setMasterRunBlock(MasterRunBlock runBlock) {
        this.master = runBlock;
    }

    public T withStatementBody(RunBlock statementBody) {
        this.body = statementBody;
        return getThis();
    }

    public MasterRunBlock getMasterBlock() {
        return master;
    }

    @Override
    public void run(VariantContainer variantContainer) {
        if (this.body != null) {
            this.body.setMasterRunBlock(this.master);
            this.body.run(variantContainer);
        }
    }

}
