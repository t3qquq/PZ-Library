// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;

public final class PZXmlUtil {
    private static boolean s_debugLogging = false;
    private static final ThreadLocal<DocumentBuilder> documentBuilders = ThreadLocal.withInitial(() -> {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            return documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException parserConfigurationException) {
            ExceptionLogger.logException(parserConfigurationException);
            throw new RuntimeException(parserConfigurationException);
        }
    });

    public static Element parseXml(String string1) throws PZXmlParserException {
        String string0 = ZomboidFileSystem.instance.resolveFileOrGUID(string1);

        Element element0;
        try {
            element0 = parseXmlInternal(string0);
        } catch (IOException | SAXException sAXException) {
            throw new PZXmlParserException("Exception thrown while parsing XML file \"" + string0 + "\"", sAXException);
        }

        element0 = includeAnotherFile(element0, string0);
        String string2 = element0.getAttribute("x_extends");
        if (string2 != null && string2.trim().length() != 0) {
            if (!ZomboidFileSystem.instance.isValidFilePathGuid(string2)) {
                string2 = ZomboidFileSystem.instance.resolveRelativePath(string0, string2);
            }

            Element element1 = parseXml(string2);
            return resolve(element0, element1);
        } else {
            return element0;
        }
    }

    private static Element includeAnotherFile(Element element0, String string1) throws PZXmlParserException {
        String string0 = element0.getAttribute("x_include");
        if (string0 != null && string0.trim().length() != 0) {
            if (!ZomboidFileSystem.instance.isValidFilePathGuid(string0)) {
                string0 = ZomboidFileSystem.instance.resolveRelativePath(string1, string0);
            }

            Element element1 = parseXml(string0);
            if (!element1.getTagName().equals(element0.getTagName())) {
                return element0;
            } else {
                Document document = createNewDocument();
                Node node0 = document.importNode(element0, true);
                Node node1 = node0.getFirstChild();

                for (Node node2 = element1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    if (node2 instanceof Element) {
                        Element element2 = (Element)document.importNode(node2, true);
                        node0.insertBefore(element2, node1);
                    }
                }

                node0.normalize();
                return (Element)node0;
            }
        } else {
            return element0;
        }
    }

    private static Element resolve(Element element1, Element element2) {
        Document document = createNewDocument();
        Element element0 = resolve(element1, element2, document);
        document.appendChild(element0);
        if (s_debugLogging) {
            System.out
                .println(
                    "PZXmlUtil.resolve> \r\n<Parent>\r\n"
                        + elementToPrettyStringSafe(element2)
                        + "\r\n</Parent>\r\n<Child>\r\n"
                        + elementToPrettyStringSafe(element1)
                        + "\r\n</Child>\r\n<Resolved>\r\n"
                        + elementToPrettyStringSafe(element0)
                        + "\r\n</Resolved>"
                );
        }

        return element0;
    }

    private static Element resolve(Element element0, Element element2, Document document) {
        if (isTextOnly(element0)) {
            return (Element)document.importNode(element0, true);
        } else {
            Element element1 = document.createElement(element0.getTagName());
            ArrayList arrayList = new ArrayList();
            NamedNodeMap namedNodeMap0 = element2.getAttributes();

            for (int int0 = 0; int0 < namedNodeMap0.getLength(); int0++) {
                Node node0 = namedNodeMap0.item(int0);
                if (!(node0 instanceof Attr)) {
                    if (s_debugLogging) {
                        System.out.println("PZXmlUtil.resolve> Skipping parent.attrib: " + node0);
                    }
                } else {
                    Attr attr0 = (Attr)document.importNode(node0, true);
                    arrayList.add(attr0);
                }
            }

            NamedNodeMap namedNodeMap1 = element0.getAttributes();

            for (int int1 = 0; int1 < namedNodeMap1.getLength(); int1++) {
                Node node1 = namedNodeMap1.item(int1);
                if (!(node1 instanceof Attr)) {
                    if (s_debugLogging) {
                        System.out.println("PZXmlUtil.resolve> Skipping attrib: " + node1);
                    }
                } else {
                    Attr attr1 = (Attr)document.importNode(node1, true);
                    String string0 = attr1.getName();
                    boolean boolean0 = true;

                    for (int int2 = 0; int2 < arrayList.size(); int2++) {
                        Attr attr2 = (Attr)arrayList.get(int2);
                        String string1 = attr2.getName();
                        if (string1.equals(string0)) {
                            arrayList.set(int2, attr1);
                            boolean0 = false;
                            break;
                        }
                    }

                    if (boolean0) {
                        arrayList.add(attr1);
                    }
                }
            }

            for (Attr attr3 : arrayList) {
                element1.setAttributeNode(attr3);
            }

            arrayList = new ArrayList();
            HashMap hashMap0 = new HashMap();

            for (Node node2 = element2.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (!(node2 instanceof Element)) {
                    if (s_debugLogging) {
                        System.out.println("PZXmlUtil.resolve> Skipping parent.node: " + node2);
                    }
                } else {
                    Element element3 = (Element)document.importNode(node2, true);
                    String string2 = element3.getTagName();
                    hashMap0.put(string2, 1 + hashMap0.getOrDefault(string2, 0));
                    arrayList.add(element3);
                }
            }

            HashMap hashMap1 = new HashMap();

            for (Node node3 = element0.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                if (!(node3 instanceof Element)) {
                    if (s_debugLogging) {
                        System.out.println("PZXmlUtil.resolve> Skipping node: " + node3);
                    }
                } else {
                    Element element4 = (Element)document.importNode(node3, true);
                    String string3 = element4.getTagName();
                    int int3 = hashMap1.getOrDefault(string3, 0);
                    int int4 = 1 + int3;
                    hashMap1.put(string3, int4);
                    int int5 = hashMap0.getOrDefault(string3, 0);
                    if (int5 < int4) {
                        arrayList.add(element4);
                    } else {
                        int int6 = 0;

                        for (int int7 = 0; int6 < arrayList.size(); int6++) {
                            Element element5 = (Element)arrayList.get(int6);
                            String string4 = element5.getTagName();
                            if (string4.equals(string3)) {
                                if (int7 == int3) {
                                    Element element6 = resolve(element4, element5, document);
                                    arrayList.set(int6, element6);
                                    break;
                                }

                                int7++;
                            }
                        }
                    }
                }
            }

            for (Element element7 : arrayList) {
                element1.appendChild(element7);
            }

            return element1;
        }
    }

    private static boolean isTextOnly(Element element) {
        boolean boolean0 = false;

        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            boolean boolean1 = false;
            if (node instanceof Text) {
                String string = node.getTextContent();
                boolean boolean2 = StringUtils.isNullOrWhitespace(string);
                boolean1 = !boolean2;
            }

            if (!boolean1) {
                boolean0 = false;
                break;
            }

            boolean0 = true;
        }

        return boolean0;
    }

    private static String elementToPrettyStringSafe(Element element) {
        try {
            return elementToPrettyString(element);
        } catch (TransformerException transformerException) {
            return null;
        }
    }

    private static String elementToPrettyString(Element element) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StreamResult streamResult = new StreamResult(new StringWriter());
        DOMSource dOMSource = new DOMSource(element);
        transformer.transform(dOMSource, streamResult);
        return streamResult.getWriter().toString();
    }

    public static Document createNewDocument() {
        DocumentBuilder documentBuilder = documentBuilders.get();
        return documentBuilder.newDocument();
    }

    private static Element parseXmlInternal(String string) throws SAXException, IOException {
        try {
            Element element0;
            try (
                FileInputStream fileInputStream = new FileInputStream(string);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ) {
                DocumentBuilder documentBuilder = documentBuilders.get();
                Document document = documentBuilder.parse(bufferedInputStream);
                bufferedInputStream.close();
                Element element1 = document.getDocumentElement();
                element1.normalize();
                element0 = element1;
            }

            return element0;
        } catch (SAXException sAXException) {
            System.err.println("Exception parsing filename: " + string);
            throw sAXException;
        }
    }

    public static void forEachElement(Element element, Consumer<Element> consumer) {
        for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element) {
                consumer.accept((Element)node);
            }
        }
    }

    public static <T> T parse(Class<T> clazz, String string) throws PZXmlParserException {
        Element element = parseXml(string);

        try {
            Unmarshaller unmarshaller = PZXmlUtil.UnmarshallerAllocator.get(clazz);
            return (T)unmarshaller.unmarshal(element);
        } catch (JAXBException jAXBException) {
            throw new PZXmlParserException("Exception thrown loading source: \"" + string + "\". Loading for type \"" + clazz + "\"", jAXBException);
        }
    }

    public static <T> void write(T object, File file) throws TransformerException, IOException, JAXBException {
        Document document = createNewDocument();
        Marshaller marshaller = PZXmlUtil.MarshallerAllocator.get(object);
        marshaller.marshal(object, document);
        write(document, file);
    }

    public static void write(Document document, File file) throws TransformerException, IOException {
        Element element = document.getDocumentElement();
        String string = elementToPrettyString(element);
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        PrintWriter printWriter = new PrintWriter(fileOutputStream);
        printWriter.write(string);
        printWriter.flush();
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static <T> boolean tryWrite(T object, File file) {
        try {
            write(object, file);
            return true;
        } catch (IOException | JAXBException | TransformerException transformerException) {
            ExceptionLogger.logException(transformerException, "Exception thrown writing data: \"" + object + "\". Out file: \"" + file + "\"");
            return false;
        }
    }

    public static boolean tryWrite(Document document, File file) {
        try {
            write(document, file);
            return true;
        } catch (IOException | TransformerException transformerException) {
            ExceptionLogger.logException(transformerException, "Exception thrown writing document: \"" + document + "\". Out file: \"" + file + "\"");
            return false;
        }
    }

    private static final class MarshallerAllocator {
        private static final ThreadLocal<PZXmlUtil.MarshallerAllocator> instance = ThreadLocal.withInitial(PZXmlUtil.MarshallerAllocator::new);
        private final Map<Class<?>, Marshaller> m_map = new HashMap<>();

        public static <T> Marshaller get(T object) throws JAXBException {
            return get(object.getClass());
        }

        public static <T> Marshaller get(Class<T> clazz) throws JAXBException {
            return instance.get().getOrCreate(clazz);
        }

        private <T> Marshaller getOrCreate(Class<T> clazz) throws JAXBException {
            Marshaller marshaller = this.m_map.get(clazz);
            if (marshaller == null) {
                JAXBContext jAXBContext = JAXBContext.newInstance(clazz);
                marshaller = jAXBContext.createMarshaller();
                marshaller.setListener(new Listener() {
                    @Override
                    public void beforeMarshal(Object object) {
                        super.beforeMarshal(object);
                    }
                });
                this.m_map.put(clazz, marshaller);
            }

            return marshaller;
        }
    }

    private static final class UnmarshallerAllocator {
        private static final ThreadLocal<PZXmlUtil.UnmarshallerAllocator> instance = ThreadLocal.withInitial(PZXmlUtil.UnmarshallerAllocator::new);
        private final Map<Class, Unmarshaller> m_map = new HashMap<>();

        public static <T> Unmarshaller get(Class<T> clazz) throws JAXBException {
            return instance.get().getOrCreate(clazz);
        }

        private <T> Unmarshaller getOrCreate(Class<T> clazz) throws JAXBException {
            Unmarshaller unmarshaller = this.m_map.get(clazz);
            if (unmarshaller == null) {
                JAXBContext jAXBContext = JAXBContext.newInstance(clazz);
                unmarshaller = jAXBContext.createUnmarshaller();
                unmarshaller.setListener(new javax.xml.bind.Unmarshaller.Listener() {
                    @Override
                    public void beforeUnmarshal(Object object0, Object object1) {
                        super.beforeUnmarshal(object0, object1);
                    }
                });
                this.m_map.put(clazz, unmarshaller);
            }

            return unmarshaller;
        }
    }
}
