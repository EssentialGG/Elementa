package club.sk1er.mods.core.universal;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class UniversalPotion {

    public static int getPotionID(PotionEffect potion) {
        //#if MC<=10809
        return potion.getPotionID();
        //#else
        //$$ return Potion.getIdFromPotion(potion.getPotion());
        //#endif
    }
}
