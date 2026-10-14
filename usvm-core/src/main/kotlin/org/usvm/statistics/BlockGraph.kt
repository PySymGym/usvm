package org.usvm.statistics

import org.usvm.StateId

interface BlockGraph<Method, Block, Statement> {
    val blocks: List<Block>
    val newBlocks: List<Block>

    fun predecessors(block: Block): Sequence<Block>
    fun successors(block: Block): Sequence<Block>

    fun callees(block: Block): Sequence<Block>
    fun callers(block: Block): Sequence<Block>

    fun returnOf(block: Block): Sequence<Block>


    fun methodOf(block: Block): Method
    fun blockOf(inst: Statement): Block
    fun statementsOf(block: Block): Sequence<Statement>
}

/**
 * A [BasicBlock] placeholder for machines that do not track basic blocks.
 */
class EmptyBlock : BasicBlock {
    override val id: Int get() = error("EmptyBlockGraph has no blocks")
    override var inCoverageZone: Boolean get() = error("EmptyBlockGraph has no blocks"); set(value) {}
    override val basicBlockSize: Int get() = error("EmptyBlockGraph has no blocks")
    override var coveredByTest: Boolean get() = error("EmptyBlockGraph has no blocks"); set(value) {}
    override var visitedByState: Boolean get() = error("EmptyBlockGraph has no blocks"); set(value) {}
    override var touchedByState: Boolean get() = error("EmptyBlockGraph has no blocks"); set(value) {}
    override val containsCall: Boolean get() = error("EmptyBlockGraph has no blocks")
    override val containsThrow: Boolean get() = error("EmptyBlockGraph has no blocks")
    override val states: MutableList<StateId> get() = error("EmptyBlockGraph has no blocks")
}

/**
 * A [BlockGraph] without blocks, for machines that do not track basic blocks.
 * The AI path selector requires a machine with a real block graph.
 */
class EmptyBlockGraph<Method, Statement> : BlockGraph<Method, EmptyBlock, Statement> {
    override val blocks: List<EmptyBlock> get() = emptyList()
    override val newBlocks: List<EmptyBlock> get() = emptyList()

    override fun predecessors(block: EmptyBlock): Sequence<EmptyBlock> = noBlocks()
    override fun successors(block: EmptyBlock): Sequence<EmptyBlock> = noBlocks()
    override fun callees(block: EmptyBlock): Sequence<EmptyBlock> = noBlocks()
    override fun callers(block: EmptyBlock): Sequence<EmptyBlock> = noBlocks()
    override fun returnOf(block: EmptyBlock): Sequence<EmptyBlock> = noBlocks()

    override fun methodOf(block: EmptyBlock): Method = noBlocks()
    override fun blockOf(inst: Statement): EmptyBlock = noBlocks()
    override fun statementsOf(block: EmptyBlock): Sequence<Statement> = noBlocks()

    private fun noBlocks(): Nothing = error("EmptyBlockGraph has no blocks")
}
