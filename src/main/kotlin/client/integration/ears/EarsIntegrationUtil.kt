package gay.pyrrha.mimic.client.integration.ears

public object EarsIntegrationUtil {
    @JvmStatic
    public val ALLOWED_PATHS: Set<String> = setOf(
        "textures/entity/player",
        "textures/entity/npc",
        "textures/entity/crypt-mimic/npc"
    )
}