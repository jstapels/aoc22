
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

}


data class OrePlan(val ore: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore

    override fun build(r: Resources) = r.copy(ore = r.ore - ore, oreRate = r.oreRate + 1)
    override fun collect(r: Resources) = r.copy(ore = r.ore + 1)

}

data class ClayPlan(val ore: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clayRate = r.clayRate + 1)
    override fun collect(r: Resources) = r.copy(clay = r.clay + 1)
}

data class ObsidianPlan(val ore: Int, val clay: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore && r.clay >= clay
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clay = r.clay - clay, obsRate = r.obsRate + 1)
    override fun collect(r: Resources) = r.copy(obs = r.obs + 1)
}

data class GeodePlan(val ore: Int, val obsidian: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore && r.obs >= obsidian
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, obs = r.obs - obsidian, geodeRate = r.geodeRate + 1)
    override fun collect(r: Resources) = r.copy(obs = r.obs + 1)
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

        val otherPlans = f.plans.reversed().drop(1)

        (1..mins).forEach { m ->
            val nextSearch = mutableListOf<Resources>()

            while (search.isNotEmpty()) {
                val r = search.removeFirst()

                if ((r.geode + r.geodeRate) < maxGeodes) continue
                if (r in visited) continue
                visited.add(r)

                val collected = r.tick()

                if (f.geodePlan.canBuild(r)) {
                    nextSearch.add(f.geodePlan.build(collected))
                } else {
                    otherPlans
                        .filter { it.canBuild(r) }
                        .mapTo(nextSearch) { it.build(collected) }
                }

                if (collected.geode > maxGeodes) {
                    maxGeodes = collected.geode
                }
            }

            search = nextSearch
        }

        return maxGeodes
    }


//    fun crackIt(f: Factory) {
//
//        var day = 1
//        var r = Resources()
//
//        var search = List<Plan>
//    }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        return data.asSequence()
            .onEach { println("Blueprint $it")}
            .map { runIt(24, it) * it.blueprint }
            .onEach { println(it) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val data = parseInput(input)
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day.pad(2)}_test")
    checkTest(33) { part1(testInput) }
    checkTest(1) { part2(testInput) }

    val input = readInput("Day${day.pad(2)}")
    solution { part1(input) }
    solution { part2(input) }
}
