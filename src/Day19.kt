
val day = 19

data class Resources(
    val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0,
    val oreBots: Int = 1, val clayBots: Int = 0, val obsidianBots: Int = 0, val geodeBots: Int = 0
) {
    fun tick() = copy(ore = ore + oreBots, clay = clay + clayBots, obsidian = obsidian + obsidianBots, geode = geode + geodeBots)
}

abstract class Plan {
    abstract fun canBuild(r: Resources): Boolean
    abstract fun build(r: Resources): Resources
    abstract fun collect(r: Resources): Resources
    abstract fun needLevel(m: Int, f: Factory, r: Resources): Double
}


data class OrePlan(val ore: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, oreBots = r.oreBots + 1)
    override fun collect(r: Resources) = r.copy(ore = r.ore + 1)

    override fun needLevel(m: Int, f: Factory, r: Resources) =
        ((f.geodePlan.ore + f.geodePlan.obsidian * f.obsidianPlan.ore + f.obsidianPlan.clay * f.clayPlan.ore).toDouble() - r.ore) /
                (r.oreBots * m)
}

data class ClayPlan(val ore: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clayBots = r.clayBots + 1)
    override fun collect(r: Resources) = r.copy(clay = r.clay + 1)
    override fun needLevel(m: Int, f: Factory, r: Resources) =
        ((f.geodePlan.obsidian * f.obsidianPlan.clay).toDouble() - r.clay) /
                (r.clayBots * m + 1)
}

data class ObsidianPlan(val ore: Int, val clay: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore && r.clay >= clay
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, clay = r.clay - clay, obsidianBots = r.obsidianBots + 1)
    override fun collect(r: Resources) = r.copy(obsidian = r.obsidian + 1)
    override fun needLevel(m: Int, f: Factory, r: Resources) =
        (f.geodePlan.obsidian.toDouble() - r.obsidian) / (r.obsidianBots * m + 1)
}

data class GeodePlan(val ore: Int, val obsidian: Int): Plan() {
    override fun canBuild(r: Resources) = r.ore >= ore && r.obsidian >= obsidian
    override fun build(r: Resources) = r.copy(ore = r.ore - ore, obsidian = r.obsidian - obsidian, geodeBots = r.geodeBots + 1)
    override fun collect(r: Resources) = r.copy(obsidian = r.obsidian + 1)
    override fun needLevel(m: Int, f: Factory, r: Resources) =
        1.0 / (r.geodeBots + 1)
}

data class Factory(val blueprint: Int, val orePlan: OrePlan, val clayPlan: ClayPlan, val obsidianPlan: ObsidianPlan, val geodePlan: GeodePlan) {
    val plans = listOf(orePlan, clayPlan, obsidianPlan, geodePlan)
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


    fun qualityLevel(minutes: Int, factory: Factory, resources: Resources): Int {
        println("Minutes $minutes:    ($resources)")
        var r = resources.tick()
        if (minutes == 24) return resources.geodeBots

        val bestPlan = factory.plans
            .maxBy { p -> p.needLevel(24 - minutes, factory, resources)
                .also { println("     ? $p -> $it")} }

        println("  picking plan $bestPlan")

        if (bestPlan.canBuild(r))
            r = bestPlan.build(r)

        return qualityLevel(minutes + 1, factory, r)
            .also { println("Minute $minutes ($it): $resources -> $r") }

//        return factory.plans.reversed()
//            .maxOf {
//                if (it.canBuild(r)) {
//                    r = it.build(r)
//                    qualityLevel(minutes + 1, factory, r)
//                } else qualityLevel(minutes + 1, factory, r)
//            }.also { println("Minute $minutes ($it): $resources -> $r") }
    }

    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        return data.asSequence()
            .onEach { println("Blueprint $it")}
            .map { qualityLevel(1, it, Resources()) }
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
