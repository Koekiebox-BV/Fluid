package com.fluidbpm.ws.client.v1.asn1der;

import com.fluidbpm.program.api.vo.field.Field;
import com.fluidbpm.program.api.vo.field.MultiChoice;
import com.fluidbpm.ws.client.v1.asn1der.vo.transmission.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class TestASNMapperChoiceRoundTrip {
    @Test public void retainsSelectionMetadataAndAvailableChoices() {
        PayloadPopulate pop = new PayloadPopulate(
            Arrays.asList(new ASNMultiChoiceField("ASN Features", Arrays.asList(
                new ASNMultiChoice(11L, "Tap"), new ASNMultiChoice(12L, "Dip")))),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Arrays.asList(new FormFieldMetaData("ASN Features", "SelectMany")));
        ASNMapperField mapper = new ASNMapperField(pop);
        Field original = new Field("ASN Features", new MultiChoice(Arrays.asList("Tap")));
        original.setTypeAsEnum(Field.Type.MultipleChoice);
        Field decoded = mapper.decode(mapper.encode(original));
        assertEquals("ASN Features", decoded.getFieldName());
        assertEquals("SelectMany", decoded.getTypeMetaData());
        assertEquals(Arrays.asList("Tap"), decoded.getFieldValueAsMultiChoice().getSelectedMultiChoices());
        assertEquals(new HashSet<>(Arrays.asList("Tap", "Dip")),
            new HashSet<>(decoded.getFieldValueAsMultiChoice().getAvailableMultiChoices()));
    }
}
