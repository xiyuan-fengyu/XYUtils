package com.xiyuan.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.HashMap;

public class VelocityUtil {

    private static final VelocityEngine ve = new VelocityEngine();

    private static final HashMap<String, Template> templates = new HashMap<>();

    static {
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
    }

    private static Template getTemplate(String resource) {
        Template template = templates.get(resource);
        if (template == null) {
            template = ve.getTemplate(resource);
            templates.put(resource, template);
        }
        return template;
    }

    public static TemplateHelper template(String resource) {
        return new TemplateHelper(getTemplate(resource));
    }

    public static class TemplateHelper {

        private final VelocityContext context;

        private final Template template;

        private TemplateHelper(Template template) {
            this.template = template;
            this.context = new VelocityContext();
        }

        public TemplateHelper put(String key, Object value) {
            this.context.put(key, value);
            return this;
        }

        public String build() {
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            return writer.toString();
        }

        @Override
        public String toString() {
            return build();
        }
    }

    public static void main(String[] args) {
        System.out.println(template("Test.vm").put("name", "world").build());
    }

}
