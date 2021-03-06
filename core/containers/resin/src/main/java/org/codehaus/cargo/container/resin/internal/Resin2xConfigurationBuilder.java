/*
 * ========================================================================
 *
 * Codehaus CARGO, copyright 2004-2011 Vincent Massol, 2012-2017 Ali Tokmen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package org.codehaus.cargo.container.resin.internal;

import java.util.Map;

import org.codehaus.cargo.container.configuration.builder.ConfigurationEntryType;
import org.codehaus.cargo.container.configuration.entry.DataSource;
import org.codehaus.cargo.container.configuration.entry.Resource;
import org.codehaus.cargo.container.property.DataSourceConverter;
import org.codehaus.cargo.container.spi.configuration.builder.AbstractConfigurationBuilder;

/**
 * Contains the xml elements used to build a normal or XA compliant DataSource for Resin.
 */
public class Resin2xConfigurationBuilder extends AbstractConfigurationBuilder
{
    /**
     * Exception message when trying to configure Transactions when not using an appropriate driver.
     */
    public static final String TRANSACTIONS_WITH_XA_OR_JCA_ONLY =
        "Resin only supports transactions with an XADataSource or ManagedConnectionFactory object";

    /**
     * used to translate DataSources into Resources
     */
    private DataSourceConverter converter = new DataSourceConverter();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toConfigurationEntry(Resource resource)
    {
        StringBuilder resourceString = new StringBuilder();
        resourceString.append("<resource-ref>\n" + "      <res-ref-name>" + resource.getName()
            + "</res-ref-name>\n");

        if (resource.getClassName() != null)
        {
            resourceString.append("      <res-type>" + resource.getClassName() + "</res-type>\n");
        }
        else
        {
            resourceString.append("      <res-type>" + resource.getType() + "</res-type>\n");
        }
        for (Map.Entry<String, String> parameter : resource.getParameters().entrySet())
        {
            resourceString.append("    <init-param ").append(parameter.getKey());
            resourceString.append("=\"").append(parameter.getValue());
            resourceString.append("\" />\n");
        }

        resourceString.append("</resource-ref>");
        return resourceString.toString();
    }

    /**
     * In Resin 2.x DataSources are Resources
     * 
     * @param ds datasource to configure
     * @return String representing the Resource representing it.
     */
    protected String toResinConfigurationEntry(DataSource ds)
    {
        Resource resource = null;
        if (ConfigurationEntryType.XA_DATASOURCE.equals(ds.getConnectionType()))
        {
            resource = converter.convertToResource(ds,
                ConfigurationEntryType.XA_DATASOURCE, "driver-name");
        }
        else
        {
            resource = converter.convertToResource(ds,
                ConfigurationEntryType.DATASOURCE, "driver-name");
        }
        return toConfigurationEntry(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildEntryForDriverConfiguredDataSourceWithNoTx(DataSource ds)
    {
        return toResinConfigurationEntry(ds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildEntryForDriverConfiguredDataSourceWithLocalTx(DataSource ds)
    {
        throw new UnsupportedOperationException(TRANSACTIONS_WITH_XA_OR_JCA_ONLY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildEntryForDriverConfiguredDataSourceWithXaTx(DataSource ds)
    {
        throw new UnsupportedOperationException(TRANSACTIONS_WITH_XA_OR_JCA_ONLY);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildConfigurationEntryForXADataSourceConfiguredDataSource(DataSource ds)
    {
        return toResinConfigurationEntry(ds);
    }

}
