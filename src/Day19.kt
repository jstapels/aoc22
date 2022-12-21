
val day = 19

data class Resources(
    val ore: Int = 0, val clay: Int = 0, val obs: Int = 0, val geode: Int = 0,
    val oreRate: Int = 1, val clayRate: Int = 0, val obsRate: Int = 0, val geodeRate: Int = 0
) {
    fun tick() =
        copy(ore = ore + oreRate, clay = clay + clayRate, obs = obs + obsRate, geode = geode + geodeRate)

    fun dump() {
        if (oreRate > 0) println("$oreRate ore-collecting robot collects $oreRate ore; you now have ${ore} ore.")
        if (clayRate > 0) println("$clayRate clay-collecting robot collects $clayRate clay; you now have ${clay} clay.")
        if (obsRate > 0) println("$obsRate obs-collecting robot collects $obsRate obs; you now have ${obs} obs.")
        if (geodeRate > 0) println("$geodeRate geode-collecting robot collects $geodeRate geode; you now have ${geode} geode.")
    }

}

abstract class Plan {
    abstract fun canBuild(r: Resources): Boolean
    abstract fun build(r: Resources): Resources
    abstract fun collect(r: Resources): Resources

    open fun shouldBuild(r: Resources, f: Factory, ml: Int) = true

    open fun minsNeeded(r: Resources) = 0
}


data class OrePlan(val ore: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore

    override fun build(r: Resources) = r.copy(ore = r.ore - ore, oreRate = r.oreRate + 1)
    override fun collect(r: Resources) = r.copy(ore = r.ore + 1)

    override fun shouldBuild(r: Resources, f: Factory, ml: Int) =
        r.ore < (maxOf(f.orePlan.ore, f.geodePlan.ore, f.obsPlan.ore, f.clayPlan.ore) - r.oreRate) * ml

    override fun minsNeeded(r: Resources) =
        ((ore - r.ore) + r.oreRate - 1) / r.oreRate
}

data class ClayPlan(val ore: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clayRate = r.clayRate + 1)
    override fun collect(r: Resources) = r.copy(clay = r.clay + 1)

    override fun shouldBuild(r: Resources, f: Factory, ml: Int) =
        r.clay < (f.obsPlan.clay - r.clayRate) * ml

    override fun minsNeeded(r: Resources) =
        ((ore - r.ore) + r.oreRate - 1) / r.oreRate
}

data class ObsidianPlan(val ore: Int, val clay: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore && r.clay >= clay
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clay = r.clay - clay, obsRate = r.obsRate + 1)
    override fun collect(r: Resources) = r.copy(obs = r.obs + 1)

    override fun shouldBuild(r: Resources, f: Factory, ml: Int) =
        r.obs < (f.geodePlan.obs - r.obsRate) * ml

    override fun minsNeeded(r: Resources) =
        maxOf(((ore - r.ore) + r.oreRate - 1) / r.oreRate,
            ((clay - r.clay) + r.clayRate - 1) / r.clayRate)
}

data class GeodePlan(val ore: Int, val obs: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore && r.obs >= obs
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, obs = r.obs - obs, geodeRate = r.geodeRate + 1)
    override fun collect(r: Resources) = r.copy(obs = r.obs + 1)

    override fun minsNeeded(r: Resources) =
        maxOf(((ore - r.ore) + r.oreRate - 1) / r.oreRate,
            ((obs - r.obs) + r.obsRate - 1) / r.obsRate)
}

data class NoPlan(val ore: Int = 0): Plan() {
    override fun canBuild(r: Resources) = true
    override fun build(r: Resources) = r
    override fun collect(r: Resources) = r
}

data class Factory(val blueprint: Int, val orePlan: OrePlan, val clayPlan: ClayPlan, val obsPlan: ObsidianPlan, val geodePlan: GeodePlan) {
    val plans = listOf(NoPlan(), orePlan, clayPlan, obsPlan, geodePlan)
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


    fun runIt(mins: Int, f: Factory): Int {

        var search = mutableListOf(Resources())
        val visited = mutableSetOf<Resources>()
        var maxGeodes = 0

        (1..mins).forEach { m ->
            val nextSearch = mutableListOf<Resources>()

            while (search.isNotEmpty()) {
                val r = search.removeFirst()

                // Not good enough
                if (r.geode + r.geodeRate + 2 < maxGeodes) continue

                // Been there done that
                if (r in visited) continue
                visited.add(r)

                val collected = r.tick()

                f.plans
                    .reversed()
                    .filter { it.canBuild(r) && it.shouldBuild(r, f, mins - m) }
                    .mapTo(nextSearch) { it.build(collected) }

                if (collected.geode > maxGeodes) {
                    maxGeodes = collected.geode
                }
            }

            search = nextSearch
        }

        return maxGeodes
    }


    var bestSoFar = 0
    fun buildIt(mins: Int, f: Factory, r: Resources = Resources()): Int {
        val plans = listOf(f.geodePlan, f.obsPlan, f.clayPlan, f.orePlan)

//        println("Mins ${25-mins} -> $r")

        if (mins <= 1) {
            val geodes = r.tick().geode
            if (geodes > bestSoFar) println("New best!! Min ${25-mins} $r")
            bestSoFar = bestSoFar.coerceAtLeast(geodes)
            return geodes
        }

        val possibleNext = plans.filter {
            when (it) {
                is GeodePlan -> {
                    (mins * r.obsRate) > it.obs &&
                            (mins * r.oreRate) > it.ore
                }
                is ObsidianPlan -> {
                    (mins * r.clayRate) > it.clay &&
                            (mins * r.oreRate) > it.ore &&
                            f.geodePlan.obs > r.obsRate
                }
                is ClayPlan -> {
                    (mins * r.oreRate) > it.ore &&
                            f.obsPlan.clay > r.clayRate
                }
                else -> {
                    maxOf(f.geodePlan.ore, f.obsPlan.ore, f.clayPlan.ore, f.orePlan.ore) > r.oreRate
                }
            }
        }

        // builds
        val best = possibleNext.maxOfOrNull {
            val minsNeeded = it.minsNeeded(r).coerceAtLeast(0)

            // fast-forward
            var nextR = r.tick()
            var m = 1
            while (m < minsNeeded && m < mins) {
                m++
                nextR = nextR.tick()
            }

            if (m == minsNeeded && m < mins) {
                nextR = it.build(nextR)
                buildIt(mins - m, f, nextR)
            } else {
                nextR.geode
            }
        } ?: r.tick().geode

        if (best > bestSoFar) println("New best!! Min ${25-mins} $r")
        bestSoFar = bestSoFar.coerceAtLeast(best)
        return best
    }


    fun part1(input: List<String>) =
        parseInput(input).asSequence()
            .onEach { println("Blueprint $it")}
            .map { buildIt(24, it) * it.blueprint }
            .onEach { println(it) }
            .sum()

    fun part2(input: List<String>) =
        parseInput(input).asSequence()
            .take(3)
            .onEach { println("Blueprint $it")}
            .map { runIt(32, it) }
            .onEach { println(it) }
            .fold(1) { a, v -> a * v}

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(33) { part1(testInput) }
    checkTest(3472) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
