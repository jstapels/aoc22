
val day = 19

data class Resources(
    val ore: Int = 0, val clay: Int = 0, val obs: Int = 0, val geodes: Int = 0,
    val oreRate: Int = 1, val clayRate: Int = 0, val obsRate: Int = 0, val geodeRate: Int = 0
) {
    fun tick(days: Int = 1) =
        copy(ore = ore + oreRate * days, clay = clay + clayRate * days, obs = obs + obsRate * days, geodes = geodes + geodeRate * days)

    fun dump() {
        if (oreRate > 0) println("$oreRate ore-collecting robot collects $oreRate ore; you now have ${ore} ore.")
        if (clayRate > 0) println("$clayRate clay-collecting robot collects $clayRate clay; you now have ${clay} clay.")
        if (obsRate > 0) println("$obsRate obs-collecting robot collects $obsRate obs; you now have ${obs} obs.")
        if (geodeRate > 0) println("$geodeRate geode-collecting robot collects $geodeRate geode; you now have ${geodes} geode.")
    }

}

abstract class Plan {
    abstract fun build(r: Resources): Resources
    abstract fun collect(r: Resources): Resources

    open fun shouldBuild(r: Resources, f: Factory, mins: Int) = true

    open fun minsNeeded(r: Resources) = 0
}


data class OrePlan(val ore: Int): Plan() {

    override fun build(r: Resources) = r.copy(ore = r.ore - ore, oreRate = r.oreRate + 1)
    override fun collect(r: Resources) = r.copy(ore = r.ore + 1)

    override fun shouldBuild(r: Resources, f: Factory, mins: Int): Boolean {
        val maxOreNeeded = maxOf(f.geodePlan.ore, f.obsPlan.ore, f.clayPlan.ore, f.orePlan.ore)

        return maxOreNeeded > r.oreRate &&
                maxOreNeeded * mins > r.oreRate * mins + r.ore
    }

    override fun minsNeeded(r: Resources) =
        ((ore - r.ore) + r.oreRate - 1) / r.oreRate
}

data class ClayPlan(val ore: Int): Plan() {
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clayRate = r.clayRate + 1)
    override fun collect(r: Resources) = r.copy(clay = r.clay + 1)

    override fun shouldBuild(r: Resources, f: Factory, mins: Int) =
        (mins * r.oreRate) > (ore - r.ore) &&
                f.obsPlan.clay > r.clayRate &&
                f.obsPlan.clay * mins > r.clayRate * mins + r.clay



    override fun minsNeeded(r: Resources) =
        ((ore - r.ore) + r.oreRate - 1) / r.oreRate
}

data class ObsidianPlan(val ore: Int, val clay: Int): Plan() {
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clay = r.clay - clay, obsRate = r.obsRate + 1)
    override fun collect(r: Resources) = r.copy(obs = r.obs + 1)

    override fun shouldBuild(r: Resources, f: Factory, mins: Int) =
        (mins * r.clayRate) > (clay - r.clay) &&
                (mins * r.oreRate) > (ore - r.ore) &&
                f.geodePlan.obs > r.obsRate &&
                (f.geodePlan.obs * mins) > r.obsRate * mins - r.obs

    override fun minsNeeded(r: Resources) =
        maxOf(((ore - r.ore) + r.oreRate - 1) / r.oreRate,
            ((clay - r.clay) + r.clayRate - 1) / r.clayRate)
}

data class GeodePlan(val ore: Int, val obs: Int): Plan() {
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, obs = r.obs - obs, geodeRate = r.geodeRate + 1)
    override fun collect(r: Resources) = r.copy(obs = r.obs + 1)

    override fun shouldBuild(r: Resources, f: Factory, mins: Int) =
        (mins * r.obsRate) > (obs - r.obs) &&
                (mins * r.oreRate) > (ore - r.ore)

    override fun minsNeeded(r: Resources) =
        maxOf(((ore - r.ore) + r.oreRate - 1) / r.oreRate,
            ((obs - r.obs) + r.obsRate - 1) / r.obsRate)
}

data class Factory(val blueprint: Int, val orePlan: OrePlan, val clayPlan: ClayPlan, val obsPlan: ObsidianPlan, val geodePlan: GeodePlan) {
    val plans = listOf(geodePlan, obsPlan, clayPlan, orePlan)
}


fun main() {
    fun parseInput(input: List<String>): List<Factory> {
        val blueprintRe = """Blueprint (\d+):""".toRegex()
        val oreRe = """ore robot costs (\d+) ore""".toRegex()
        val clayRe = """clay robot costs (\d+) ore""".toRegex()
        val obsidianRe = """obsidian robot costs (\d+) ore and (\d+) clay""".toRegex()
        val geodeRe = """geode robot costs (\d+) ore and (\d+) obsidian""".toRegex()

        return input.map {
            val (blueprint) = blueprintRe.find(it)!!.destructured
            val (oreOre) = oreRe.find(it)!!.destructured
            val (clayOre) = clayRe.find(it)!!.destructured
            val (obsOre, obsClay) = obsidianRe.find(it)!!.destructured
            val (geodeOre, geodeObs) = geodeRe.find(it)!!.destructured
            Factory(blueprint.toInt(),
                OrePlan(oreOre.toInt()),
                ClayPlan(clayOre.toInt()),
                ObsidianPlan(obsOre.toInt(), obsClay.toInt()),
                GeodePlan(geodeOre.toInt(), geodeObs.toInt()))
        }
    }

    data class Best(var num: Int = 0)

    fun buildIt(mins: Int, f: Factory, r: Resources = Resources(), best: Best = Best()): Int {
        if (mins <= 1) {
            val geodes = r.tick(mins).geodes
            best.num = best.num.coerceAtLeast(geodes)
            return geodes
        }

        // best chance?
        if ((r.geodes + ((r.geodeRate + (mins / 2)) * mins)) < best.num) {
            return -1
        }

        val possibleNext = f.plans.filter { it.shouldBuild(r, f, mins) }

        // builds
        val geodes = possibleNext.maxOfOrNull {
            val minsNeeded = (it.minsNeeded(r).coerceAtLeast(0) + 1).coerceAtMost(mins)
            val nextR = it.build(r.tick(minsNeeded))
            buildIt(mins - minsNeeded, f, nextR, best)
        } ?: r.tick(mins).geodes

        return geodes
    }


    fun part1(input: List<String>) =
        parseInput(input).asSequence()
            .onEach { debug("Blueprint $it")}
            .map { buildIt(24, it) * it.blueprint }
            .onEach { debug("buildIt -> $it\n") }
            .sum()

    fun part2(input: List<String>) =
        parseInput(input).asSequence()
            .take(3)
            .onEach { debug("Blueprint $it")}
            .map { buildIt(32, it) }
            .onEach { debug("buildIt -> $it\n") }
            .fold(1) { a, v -> a * v}

    // test if implementation meets criteria from the description, like:
    println("Output for Day $day")
    println("-".repeat(64))

    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(33) { part1(testInput) }
    checkTest(3472) { part2(testInput) }

    println("-".repeat(64))

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }

    println("-".repeat(64))
}
