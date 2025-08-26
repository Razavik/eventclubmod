package com.eventclubmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.particle.ParticleTypes;

/**
 * Энтити "Тень": неподвижный гуманоид, без ИИ, без гравитации/коллизии.
 */
public class ShadowNpcEntity extends MobEntity {

    public ShadowNpcEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        this.noClip = true;
        this.setNoGravity(true);
        this.setPersistent();
    }

    // Базовые атрибуты (минимальные), чтобы корректно инициализировался Living/Mob
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.0);
    }

    @Override
    protected void initGoals() {
        // Нет целей/поведения — оставляем пустым
    }

    @Override
    public void tick() {
        super.tick();
        // Остаёмся на месте; без гравитации и толчков
        this.noClip = true;
        this.setNoGravity(true);
        this.setVelocity(0, 0, 0);
        this.fallDistance = 0;

        // Поза: приседание, чтобы визуально опустить голову
        // Примечание: это клиент-независимая поза, корректно отображается в модели игрока
        this.setPose(EntityPose.CROUCHING);

        // Фиксируем ориентацию (не "смотрим" на цели), голова не двигается относительно тела
        this.headYaw = this.bodyYaw;
        // Опускаем голову: в Minecraft положительный pitch — вниз
        this.setPitch(30.0f);

        // Визуальный эффект: мелкие частицы дыма, поднимающиеся вертикальной колонной на всю высоту
        if ((this.age % 4 == 0)) {
            var w = this.getWorld();
            if (!w.isClient && w instanceof ServerWorld sw) {
                double baseX = this.getX();
                double baseY = this.getY();
                double baseZ = this.getZ();
                // Несколько "сечений" по высоте сущности (около 1.8 блока)
                for (double dy = 0.2; dy <= 1.8; dy += 0.3) {
                    sw.spawnParticles(
                            ParticleTypes.SMOKE,
                            baseX,
                            baseY + dy,
                            baseZ,
                            1,            // маленькое количество в каждом слое
                            0.06, 0.0, 0.06, // узкий радиус по X/Z, без вертикального разброса
                            0.02          // небольшая скорость: частицы будут медленно тянуться вверх
                    );
                }
            }
        }
    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    // Нельзя нанести урон (актуальная сигнатура для 1.21.x)
    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false; // полностью игнорируем входящий урон
    }

    // Не может быть законной целью атаки (клик мечом и т.п.)
    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.block.BlockState state) {
        // без звуков шагов
    }
}
