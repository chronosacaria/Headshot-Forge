package chronosacaria.headshot.events;

import chronosacaria.headshot.Headshot;
import chronosacaria.headshot.config.HeadshotConfig;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Headshot.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DealHeadshotEvent {

    @SubscribeEvent
    public static void onHeadshotIfApplicable(LivingDamageEvent event){
        if (event.getSource().isProjectile()){
            Entity trueSource = event.getSource().getEntity();

            Entity entity = event.getEntityLiving();

            double headStart =
                    entity.position().add(0.0, entity.getDimensions(entity.getPose()).height * 0.85, 0.0).y - 0.17;

            if (doesNotHaveHelmet(event.getEntity())
                    && event.getSource().getSourcePosition().y > headStart
                    && event.getSource() != null
                    && !((LivingEntity)entity).isDamageSourceBlocked(event.getSource())){
                if (event.getEntity() instanceof Animal
                        || event.getEntity() instanceof WaterAnimal
                        || event.getEntity() instanceof Slime
                        || event.getEntity() instanceof EnderDragon) return;
                if (event.getSource().getSourcePosition().y > headStart) {
                    if (entity instanceof ServerPlayer){
                        ((ServerPlayer)entity).displayClientMessage(new TextComponent("You got headshot!"), true);
                    }
                }
                if (event.getSource().getEntity() instanceof ServerPlayer && trueSource != null){
                    ((ServerPlayer)trueSource).displayClientMessage(new TextComponent("Headshot!"), true);
                }
                double headshotDamage = event.getAmount() * HeadshotConfig.HEADSHOT_DAMAGE_MULTIPLIER.get();
                double projectileProtectionDamageReduction =
                        HeadshotConfig.HEADSHOT_PROJECTILE_PROTECTION_DAMAGE_REDUCTION.get();

                if (hasProjectileProtection(event.getEntity())){
                    event.setAmount((float)  headshotDamage * (float) projectileProtectionDamageReduction);
                    event.getEntityLiving()
                            .getItemBySlot(EquipmentSlot.HEAD)
                            .hurt(
                                    (int) headshotDamage / 4,
                                    event.getEntity().getCommandSenderWorld().random,
                                    null);
                } else if (!hasProjectileProtection(event.getEntity())){
                    event.setAmount((float)headshotDamage);
                    event.getEntityLiving()
                            .getItemBySlot(EquipmentSlot.HEAD)
                            .hurt((int) headshotDamage / 2,
                                    event.getEntity().getCommandSenderWorld().random,
                                    null);

                    if (HeadshotConfig.DO_BLINDNESS.get()){
                        ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                                HeadshotConfig.BLIND_TICKS.get(), 3));
                    }

                    if (HeadshotConfig.DO_NAUSEA.get()){
                        ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.CONFUSION,
                                HeadshotConfig.NAUSEA_TICKS.get(), 3));
                    }
                }
            }
        }
    }
    private static boolean doesNotHaveHelmet(Entity entity){
        if (HeadshotConfig.HELMET_MITIGATION.get()){
            return ((LivingEntity)entity).getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        }
        return entity instanceof LivingEntity;
    }

    private static boolean hasProjectileProtection(Entity entity){
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, (LivingEntity) entity) > 0;
    }
}
