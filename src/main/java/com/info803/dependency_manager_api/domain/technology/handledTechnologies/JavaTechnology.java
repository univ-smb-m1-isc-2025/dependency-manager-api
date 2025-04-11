package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.dependency.JavaDependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

@Component
public class JavaTechnology extends AbstractTechnology{

    public JavaTechnology() {
        super("Java", "dependencies", Arrays.asList("pom.xml", "build.gradle"));
    }

    @Override
    public List<Dependency> extractDependencies(String content) {
        List<Dependency> dependencies = new ArrayList<>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(content)));
            System.out.println(doc.toString());
            NodeList nodes = doc.getElementsByTagName("dependency");
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                String groupId = getTagValue("groupId", element);
                String artifactId = getTagValue("artifactId", element);
                String version = getTagValue("version", element);

                Dependency dependency = new JavaDependency(artifactId, version, groupId);
                dependencies.add(dependency);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
        
        return dependencies;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag);
        return (nodes.getLength() > 0) ? nodes.item(0).getTextContent() : null;
    }
}
