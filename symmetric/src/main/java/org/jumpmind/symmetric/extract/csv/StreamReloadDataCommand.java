/*
 * SymmetricDS is an open source database synchronization solution.
 *   
 * Copyright (C) Chris Henson <chenson42@users.sourceforge.net>
 * Copyright (C) Andrew Wilcox <andrewbwilcox@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.jumpmind.symmetric.extract.csv;

import java.io.BufferedWriter;
import java.io.IOException;

import org.jumpmind.symmetric.extract.DataExtractorContext;
import org.jumpmind.symmetric.model.Data;
import org.jumpmind.symmetric.model.Node;
import org.jumpmind.symmetric.model.Trigger;
import org.jumpmind.symmetric.service.IConfigurationService;
import org.jumpmind.symmetric.service.IDataExtractorService;
import org.jumpmind.symmetric.service.INodeService;

class StreamReloadDataCommand extends AbstractStreamDataCommand {

    private IDataExtractorService dataExtractorService;

    private IConfigurationService configurationService;

    private INodeService nodeService;

    public void execute(BufferedWriter out, Data data, DataExtractorContext context) throws IOException {
        int id = data.getTriggerHistory().getTriggerId();
        Trigger trigger = configurationService.getTriggerById(id);
        if (trigger != null) {
            // The initial_load_select can be overridden
            if (data.getRowData() != null) {
                trigger.setInitialLoadSelect(data.getRowData());
            }
            Node node = nodeService.findNode(context.getBatch().getNodeId());
            dataExtractorService.extractInitialLoadWithinBatchFor(node, trigger, out, context);
            out.flush();
        } else {
            logger.error(String.format("The trigger %s is not longer available for an initial load.", id));
        }
    }

    public void setDataExtractorService(IDataExtractorService dataExtractorService) {
        this.dataExtractorService = dataExtractorService;
    }

    public void setConfigurationService(IConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setNodeService(INodeService nodeService) {
        this.nodeService = nodeService;
    }

}