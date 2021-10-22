package chronosacaria.headshot.events;

import chronosacaria.headshot.Headshot;
import chronosacaria.headshot.config.HeadshotConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Headshot.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DealHeadshotEvent {
    @SubscribeEvent
    public static void onHeadshotIfApplicable(LivingDamageEvent event){

        boolean ignore = false;

        if (event.getSource().isProjectile()) {
            Entity trueSource = event.getSource().getTrueSource();
            Entity entity = event.getEntityLiving();
            double headStart = entity.getPositionVec().add(0.0, entity.getSize(entity.getPose()).height * 0.85, 0.0).y - 0.17;
                if (!ignore && doesNotHaveHelmet(event.getEntity())
                        && event.getSource().getDamageLocation().y > headStart
                        && event.getSource() != null
                        && !((LivingEntity) entity).canBlockDamageSource(event.getSource())) {
                    if (event.getEntity() instanceof AnimalEntity
                            || event.getEntity() instanceof WaterMobEntity
                            || event.getEntity() instanceof SlimeEntity
                            || event.getEntity() instanceof EnderDragonEntity) return;
                    if (event.getSource().getDamageLocation().y > headStart){
                        if (entity instanceof ServerPlayerEntity) {
                            ((ServerPlayerEntity) entity).sendStatusMessage(new StringTextComponent("You got headshot!"),
                                    true);
                        }
                    }
                    if (event.getSource().getTrueSource() instanceof ServerPlayerEntity && trueSource != null) {
                        ((ServerPlayerEntity)trueSource).sendStatusMessage(new StringTextComponent("Headshot!"),
                                true);
                    }
                    double headshotDamage = event.getAmount() * HeadshotConfig.HEADSHOT_DAMAGE_MULTIPLIER.get();
                    event.setAmount((float)headshotDamage);
                    event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.HEAD).attemptDamageItem(((int)headshotDamage/2), event.getEntity().getEntityWorld().rand, null);
                    ignore = true;

                    if (HeadshotConfig.DO_BLINDNESS.get()) {
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.BLINDNESS,
                                HeadshotConfig.BLIND_TICKS.get(), 3));
                    }
                    if (HeadshotConfig.DO_NAUSEA.get()) {
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.NAUSEA,
                                HeadshotConfig.NAUSEA_TICKS.get(), 2));
                    }
                    return;
            }
            ignore = false;
        }

    }
    private static boolean doesNotHaveHelmet(Entity entity){
        if (HeadshotConfig.HELMET_MITIGATION.get()) {
            return ((LivingEntity) entity).getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty();
        }
        return entity instanceof LivingEntity;
    }
}
