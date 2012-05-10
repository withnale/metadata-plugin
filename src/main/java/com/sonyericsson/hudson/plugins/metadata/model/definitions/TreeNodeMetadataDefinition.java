/*
 *  The MIT License
 *
 *  Copyright 2011 Sony Ericsson Mobile Communications. All rights reserved.
 *  Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Constants;
import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.ParentUtil;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Hudson;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A metadata definition containing other metadata definitions as children.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(
        value = "UG_SYNC_SET_UNSYNC_GET",
        justification = "It is synchronized")
public class TreeNodeMetadataDefinition extends AbstractMetadataDefinition
        implements MetadataParent<MetadataDefinition> {

    private List<MetadataDefinition> children;

    /**
     * Standard constructor.
     * @param name the name.
     * @param description the description.
     * @param children the children of this node.
     */
    @DataBoundConstructor
    public TreeNodeMetadataDefinition(String name, String description, List<MetadataDefinition> children) {
        super(name, description);
        this.children = children;
    }

    /**
     * Standard constructor.
     *
     * @param name the name.
     */
    public TreeNodeMetadataDefinition(String name) {
        super(name);
        this.children = new LinkedList<MetadataDefinition>();

    }
    /**
     * Standard Constructor.
     *
     * @param name        the name
     * @param description the description.
     */
    public TreeNodeMetadataDefinition(String name, String description) {
        super(name, description);
        this.children = new LinkedList<MetadataDefinition>();
    }

    /**
     * Standard constructor.
     *
     * @param name     the name.
     * @param children the children of this node.
     */
    public TreeNodeMetadataDefinition(String name, List<MetadataDefinition> children) {
        super(name);
        this.children = children;
    }

    @Override
    public synchronized MetadataDefinition getChild(String name) {
        return ParentUtil.getChildValue(children, name);
    }

    @Override
    public synchronized int indexOf(String name) {
        return ParentUtil.getChildIndex(children, name);
    }

    @Override
    public synchronized MetadataDefinition setChild(int index, MetadataDefinition value) {
        return children.set(index, value);
    }

    @Override
    public synchronized Collection<MetadataDefinition> addChild(MetadataDefinition definition) {
        return ParentUtil.addChildValue(this, children, definition);
    }

    @Override
    public synchronized Collection<MetadataDefinition> addChildren(Collection<MetadataDefinition> definitions) {
        return ParentUtil.addChildValues(this, children, definitions);
    }

    /**
     *  Getter for the children of this tree node, used by stapler to create the entire tree.
      * @return the children.
     */
    public synchronized Collection<MetadataDefinition> getChildren() {
        return children;
    }

    @Override
    public Collection<String> getChildNames() {
        List<String> list = new LinkedList<String>();
        if (children != null) {
            for (MetadataDefinition def : children) {
                list.add(def.getName());
            }
        }
        return list;
    }

    @Override
    public synchronized AbstractMetadataValue createValue(StaplerRequest req, JSONObject jo) {
        TreeNodeMetadataValue value = req.bindJSON(TreeNodeMetadataValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

    @Override
    public AbstractMetadataValue createValue(StaplerRequest req) {
        return null;

    }

    /**
     * This function will generate the full name.
     *
     * @return the full name.
     */
    @Exported
    public synchronized String getFullName() {
        MetadataParent parent = getParent();
        if (parent != null) {
            return parent.getFullName() + Constants.DISPLAY_NAME_SEPARATOR + getName();
        }
        return getName();
    }

    @Override
    public String getFullNameFrom(MetadataParent<MetadataDefinition> base) {
        MetadataParent parent = getParent();
        if (parent == null || parent == base) {
            return getName();
        } else {
            return parent.getFullNameFrom(base) + Constants.DISPLAY_NAME_SEPARATOR + getName();
        }
    }

    @Override
    public JSON toJson() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public boolean requiresReplacement() {
        return false;
    }

    /**
     * The Descriptor.
     */
    @Extension
    public static class TreeNodeMetaDataDefinitionDescriptor extends
            AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.TreeNodeMetadataDefinition_DisplayName();
        }

        /**
         * Returns all the registered meta data definition descriptors. For use in a hetero-list.
         *
         * @param request the current request.
         * @return the descriptors.
         */
        public List<AbstractMetaDataDefinitionDescriptor> getDefinitionDescriptors(StaplerRequest request) {
            List<AbstractMetaDataDefinitionDescriptor> list = new LinkedList<AbstractMetaDataDefinitionDescriptor>();
            ExtensionList<AbstractMetaDataDefinitionDescriptor> extensionList =
                    Hudson.getInstance().getExtensionList(AbstractMetaDataDefinitionDescriptor.class);
            for (AbstractMetaDataDefinitionDescriptor d : extensionList) {
                list.add(d);
            }
            return list;
        }
    }
}
