package treemaker;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by lick on 08.04.17.
 */
public class XmlMaker {

    private CustomTextField root;
    private Document doc;

    XmlMaker (CustomTextField root) {
        this.root = root;
    }

    public void generate() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("goal");
//            doc.appendChild(rootElement);
            processCustomTextView(rootElement, root, "goal", true);

//            //  supercars element
//            Element supercar = doc.createElement("supercars");
//            rootElement.appendChild(supercar);
//
//            // carname element
//            Element carname = doc.createElement("carname");
//            Attr attrType = doc.createAttribute("type");
//            attrType.setValue("formula one");
//            carname.setAttributeNode(attrType);
//            carname.appendChild(
//                    doc.createTextNode("Ferrari 101"));
//            supercar.appendChild(carname);

            // write the content into xml file
            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result =
                    new StreamResult(new File("data.xml"));
            transformer.transform(source, result);
            // Output to console for testing
            StreamResult consoleResult =
                    new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processCustomTextView(Element parentElement, CustomTextField customTextField, String title, boolean isRoot) {
        CustomFieldParts customFieldParts = new CustomFieldParts(customTextField);
        int comparisonCount = customFieldParts.getComparisonCount();

        Element child = doc.createElement(title);
        Attr attrName = doc.createAttribute("name");
        attrName.setValue(customFieldParts.getName());
        child.setAttributeNode(attrName);

        Element comparisons = doc.createElement("comparisons");
        Attr attrN = doc.createAttribute("n");
        attrN.setValue(comparisonCount+"");
        comparisons.setAttributeNode(attrN);


        for(double ratio : customFieldParts.getRatios()) {
            Element ratioElement = doc.createElement("ratio");
            ratioElement.appendChild(doc.createTextNode(ratio + ""));
            comparisons.appendChild(ratioElement);
        }

        child.appendChild(comparisons);

        //if(customTextField.hasAnyChildren() && customTextField.getChildrenCount()>0) {
        for(CustomTextField ctf : customTextField.getChildCustomTextFields()) {
            processCustomTextView(child,ctf,"criterium", false);
        }

        if(isRoot)
            doc.appendChild(child);
        else
            parentElement.appendChild(child);
    }
}
