package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import pcgen.cdom.enumeration.EquipmentLocation;

public class EquipmentTest {
    @Test
    void setLocationContainedSetsLocationToCarriedNeither(){
        Equipment equipment = new Equipment();
        equipment.setLocation(EquipmentLocation.CONTAINED);

        assertEquals(EquipmentLocation.CARRIED_NEITHER, equipment.getLocation());
        assertFalse(equipment.isEquipped());
    }

    @Test
    void setNumberEquippedPositiveAlsoEquips(){
        Equipment equipment = new Equipment();
        equipment.setNumberEquipped(1);

        assertTrue(equipment.isEquipped());
    }

    @Test
    void setLocationEquippedTwoHandsAlsoEquips(){
        Equipment equipment = new Equipment();
        equipment.setLocation(EquipmentLocation.EQUIPPED_TWO_HANDS);

        assertEquals(EquipmentLocation.EQUIPPED_TWO_HANDS, equipment.getLocation());
        assertTrue(equipment.isEquipped());
    }

    @Test
    void getParentNameIsCarriedWhenCarriedNumberIsSet(){
        Equipment equipment = new Equipment();
        equipment.setNumberCarried(1.0f);

        assertEquals("Carried", equipment.getParentName());
    }

    @Test
    void typeIndexReturnsEmptyStringWhenImputInvalid(){
        Equipment equipment = new Equipment();

        assertEquals("", equipment.typeIndex(-1));
    }
}


