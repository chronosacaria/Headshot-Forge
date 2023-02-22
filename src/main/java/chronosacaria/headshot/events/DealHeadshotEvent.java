package chronosacaria.headshot.events;

import chronosacaria.headshot.Headshot;
import chronosacaria.headshot.config.HeadshotConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Headshot.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DealHeadshotEvent {
    @SubscribeEvent
    public static void onHeadshotIfApplicable(LivingDamageEvent event){

        boolean ignore = false;

        if (event.getSource().isProjectile()) {
            // Firing Entity or Projectile in the case of no True Source
            Entity trueSource = event.getSource().getTrueSource();

            // Target Entity
            LivingEntity entity = event.getEntityLiving();

            // Isolation of the head of bipedal mobs
            double headStart = entity.getPositionVec().add(0.0, entity.getSize(entity.getPose()).height * 0.85, 0.0).y - 0.17;

            // Determining if the target is wearing a helmet. If the target does not have a helmet on, do X
            if (!ignore && doesNotHaveHelmet(event.getEntity())
                    && Objects.requireNonNull(event.getSource().getDamageLocation()).y > headStart // Is the damage location the head?
                    && event.getSource() != null // Is the source valid?
                    && !entity.canBlockDamageSource(event.getSource())) { // Is the target blocking the projectile with a shield?
                if (event.getEntity() instanceof AnimalEntity                       // Is the target quadrupedal?
                        || event.getEntity() instanceof WaterMobEntity              // Is the target a water mob?
                        || event.getEntity() instanceof SlimeEntity                 // Is the target a slime?
                        || event.getEntity() instanceof EnderDragonEntity) return;  // Is the target the Ender Dragon?
                if (event.getSource().getDamageLocation().y > headStart) { // Did the arrow hit the target's head?
                    // Message sent to Player who made the headshot
                    if (entity instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) entity).sendStatusMessage(new TranslationTextComponent("event_message.headshot.headshot_on_player"),
                                true);
                    }
                }
                // Message sent to the Player who was headshot
                if (event.getSource().getTrueSource() instanceof ServerPlayerEntity && trueSource != null) {
                    ((ServerPlayerEntity) trueSource).sendStatusMessage(new TranslationTextComponent("event_message.headshot.headshot_on_entity"),
                            true);
                }
                // Headshot damage configuration
                double headshotDamage = event.getAmount() * HeadshotConfig.HEADSHOT_DAMAGE_MULTIPLIER.get();
                double projectileProtectionDamageReduction =
                        HeadshotConfig.HEADSHOT_PROJECTILE_PROTECTION_DAMAGE_REDUCTION.get();

                if (hasProjectileProtection(event.getEntity())) { // Does the target have Projectile Protection? If so, do X
                    event.setAmount((float) headshotDamage * (float) projectileProtectionDamageReduction); // Damage is halved with Projectile Protection

                    /* Extra damage is done to helmets with a headshot.
                     * With Projectile Protection, that damage is cut to 25% of
                     * damage received.
                     */
                    event.getEntityLiving()
                            .getItemStackFromSlot(EquipmentSlotType.HEAD)
                            .attemptDamageItem(
                                    ((int) headshotDamage / 4),
                                    event.getEntity().getEntityWorld().rand,
                                    null);
                } else if (!(hasProjectileProtection(event.getEntity()))) { // Does the target have Projectile Protection? If not, to Y
                    event.setAmount((float) headshotDamage); // Full damage without Projectile Protection

                    /* Extra damage is done to helmets with a headshot.
                     * Without Projectile Protection, that damage is cut to 50% of
                     * damage received.
                     */
                    event.getEntityLiving()
                            .getItemStackFromSlot(EquipmentSlotType.HEAD)
                            .attemptDamageItem(
                                    ((int) headshotDamage / 2),
                                    event.getEntity().getEntityWorld().rand,
                                    null);
                    ignore = true;

                    // Optional Blindness Effect
                    if (HeadshotConfig.DO_BLINDNESS.get()) {
                        entity.addPotionEffect(new EffectInstance(Effects.BLINDNESS,
                                HeadshotConfig.BLIND_TICKS.get(), 3));
                    }

                    // Optional Nausea Effect
                    if (HeadshotConfig.DO_NAUSEA.get()) {
                        entity.addPotionEffect(new EffectInstance(Effects.NAUSEA,
                                HeadshotConfig.NAUSEA_TICKS.get(), 2));
                    }
                    return;
                }
                ignore = false;
            }
        }

    }
    private static boolean doesNotHaveHelmet(Entity entity){
        if (HeadshotConfig.HELMET_MITIGATION.get()) {
            return ((LivingEntity) entity).getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty();
        }
        return entity instanceof LivingEntity;
    }

    private static boolean hasProjectileProtection(Entity entity){
        return EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, (LivingEntity) entity) > 0;

    }
}
