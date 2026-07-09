// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.ZomboidFileSystem;
import zombie.core.Rand;

@XmlRootElement
public class ItemManager {
    public ArrayList<CarriedItem> m_Items = new ArrayList<>();
    @XmlTransient
    public static ItemManager instance;

    public static void init() {
        File file = ZomboidFileSystem.instance.getMediaFile("items" + File.separator + "items.xml");
        instance = Parse(file.getPath());
    }

    public CarriedItem GetRandomItem() {
        int int0 = Rand.Next(this.m_Items.size() + 1);
        return int0 < this.m_Items.size() ? this.m_Items.get(int0) : null;
    }

    public static ItemManager Parse(String string) {
        try {
            return parse(string);
        } catch (JAXBException jAXBException) {
            jAXBException.printStackTrace();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        return null;
    }

    public static ItemManager parse(String string) throws JAXBException, IOException {
        ItemManager itemManager0;
        try (FileInputStream fileInputStream = new FileInputStream(string)) {
            JAXBContext jAXBContext = JAXBContext.newInstance(ItemManager.class);
            Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
            ItemManager itemManager1 = (ItemManager)unmarshaller.unmarshal(fileInputStream);
            itemManager0 = itemManager1;
        }

        return itemManager0;
    }

    public static void Write(ItemManager itemManager, String string) {
        try {
            write(itemManager, string);
        } catch (JAXBException jAXBException) {
            jAXBException.printStackTrace();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public static void write(ItemManager itemManager, String string) throws IOException, JAXBException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(string)) {
            JAXBContext jAXBContext = JAXBContext.newInstance(ItemManager.class);
            Marshaller marshaller = jAXBContext.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            marshaller.marshal(itemManager, fileOutputStream);
        }
    }
}
