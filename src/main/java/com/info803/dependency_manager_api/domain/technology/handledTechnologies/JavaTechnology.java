package com.info803.dependency_manager_api.domain.technology.handledTechnologies;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;

import org.springframework.stereotype.Component;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyExtractDependenciesException;
import com.info803.dependency_manager_api.adapters.api.exception.customs.technology.TechnologyUpdateDependenciesException;
import com.info803.dependency_manager_api.domain.dependency.Dependency;
import com.info803.dependency_manager_api.domain.dependency.JavaDependency;
import com.info803.dependency_manager_api.domain.technology.AbstractTechnology;

@Component
public class JavaTechnology extends AbstractTechnology{

    public JavaTechnology() {
        super("Java", "dependencies", Arrays.asList("pom.xml", "build.gradle"));
    }

    @Override
    public List<Dependency> extractDependencies(String content) throws TechnologyExtractDependenciesException {
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
            throw new TechnologyExtractDependenciesException("Error extracting dependencies : " + e.getMessage(), e);
        }
        return dependencies;
        
    }

    @Override
    public void updateDependencies(List<Dependency> dependencies) throws TechnologyUpdateDependenciesException {
        // Steps :
        // 1. For each file in filesPaths, read the file
        // 2. For each dependency in dependencies, check if it is in the file
        // 3. If it is, replace the dependency with the new version (if current is null don't replace)
        // 4. Write the file
        try {
            for (String file : filesPaths) {
                String content = FileUtils.readFileToString(new File(file), "UTF-8");
                for (Dependency dependency : dependencies) {
                    if (dependency.getCurrent() != null) {
                        content = content.replace(dependency.getCurrent(), dependency.getLatest());
                    }
                }
                FileUtils.writeStringToFile(new File(file), content, "UTF-8");

            }
        } catch (Exception e) {
            throw new TechnologyUpdateDependenciesException("Error updating dependencies : " + e.getMessage(), e);
        }
    }

    @Override
    public AbstractTechnology copy() {
        return new JavaTechnology();
    }

    // ---- Private methods ----
    private String getTagValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag);
        return (nodes.getLength() > 0) ? nodes.item(0).getTextContent() : null;
    }
}
