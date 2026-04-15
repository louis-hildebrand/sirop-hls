package mhir.ir

trait StmBuildUtils {

  /** Helper methods for [[StmBuild]].
    */
  implicit class StmBuildUtilsImplicit(stm: StmBuild) {

    /** Construct a new <code>StmBuild</code> that is equivalent to this one but
      * where all the accumulator variables have been replaced by fresh
      * variables.
      */
    def renameVars: StmBuild = {
      this.stm.renameVars(this.stm.accVars.map(x => x -> x.freshCopy).toMap)
    }

    /** Construct a new <code>StmBuild</code> that is equivalent to this one but
      * where the accumulator variable <code>x</code> has been replaced by a
      * fresh variable.
      */
    private def renameVar(x: Param): StmBuild = {
      renameVars(Map(x -> x.freshCopy))
    }

    /** Rename all the bound variables in this stream using the given
      * substitutions.
      *
      * @param replacements
      *   A map from old variables to new variables.
      */
    def renameVars(replacements: Map[Param, Param]): StmBuild = {
      // No canonicalization should be required here
      implicit val c: Canonicalizer = NoOpCanonicalizer
      require(
        replacements.keys.forall(x => this.stm.accVars.contains(x)),
        "all the variables to be replaced must appear in this stream"
      )
      val subs: Map[Expr, Expr] = replacements.toMap
      val newData = this.stm.data.subPreserveType(subs)
      val newValid = this.stm.valid.subPreserveType(subs)
      val newEquations = this.stm.equations.map({ case (x, (z, next)) =>
        val y = replacements.getOrElse(x, x).rebuild(x.typ).asInstanceOf[Param]
        y -> (z, next.subPreserveType(subs))
      })
      StmBuild(this.stm.n, newData, newValid, newEquations)(this.stm.typ)
    }

    def replaceVars(replacements: Map[Param, Expr]): StmBuild = {
      // No canonicalization should be required here; accumulator variables
      // should not be part of the type of an expression
      implicit val c: Canonicalizer = NoOpCanonicalizer
      if (replacements.keys.exists(x => !this.stm.accVars.contains(x))) {
        val xs =
          replacements.keys.filter(x => !this.stm.accVars.contains(x)).toSeq
        throw new IllegalArgumentException(
          s"Cannot replace variables $xs because they are not part of the stream."
            + s" The stream is $this."
        )
      } else {
        val subs: Map[Expr, Expr] = replacements.toMap
        StmBuild(
          this.stm.n,
          this.stm.data.subPreserveType(subs),
          this.stm.valid.subPreserveType(subs),
          this.stm.equations
            .filter({ case (x, _) => !replacements.contains(x) })
            .map({ case (x, (z, next)) =>
              x -> (z, next.subPreserveType(subs))
            })
        )(annotations = this.stm.annotations)
      }
    }

    def replaceVar(x: Param, e: Expr): StmBuild = replaceVars(Map(x -> e))

    /** Add a new equation to this stream whose value is the number of valid
      * outputs that this stream has <i>previously</i> produced.
      *
      * @param outCtr
      *   The variable to use for the new equation. If the variable already
      *   appears bound in this stream, then the bound variable will be renamed.
      */
    def addOutputCounter(outCtr: Param): StmBuild = {
      this.stm.requireType("adding an output counter")
      outCtr.typ match {
        case Missing =>
          throw new IllegalArgumentException(
            s"Variable provided for output counter must have a type."
            // ... because every accumulator must have a type, and how would we
            // know what value to choose here?
          )
        case TyUInt(0) =>
          throw new IllegalArgumentException(
            s"Cannot add zero-width output counter."
          )
        case _: TyUInt => ()
        case t =>
          throw new IllegalArgumentException(
            s"Variable provided for output counter has type $t."
              + " Expected an unsigned integer."
          )
      }
      val s =
        if (this.stm.equations.contains(outCtr))
          this.renameVar(outCtr)
        else
          this.stm
      val z = C(0)(outCtr.typ)
      val next = Mux(
        s.valid,
        Sum(C(1)(outCtr.typ), outCtr)(outCtr.typ),
        outCtr
      )(outCtr.typ)
      s.addAccumulator(outCtr, z, next)
    }

    /** Add a new equation to this stream whose value is the number of inputs
      * that this stream has <i>previously</i> read from the input stream
      * represented by <code>x</code>.
      *
      * @param x
      *   The input stream.
      * @param inCtr
      *   The variable to use for the new equation. If the variable already
      *   appears bound in this stream, then the bound variable will be renamed.
      */
    def addInputCounter(x: Param, inCtr: Param): StmBuild = {
      this.stm.requireType("adding an input counter")
      inCtr.typ match {
        case Missing =>
          throw new IllegalArgumentException(
            s"Variable provided for output counter must have a type."
            // ... because every accumulator must have a type, and how would we
            // know what value to choose here?
          )
        case TyUInt(0) =>
          throw new IllegalArgumentException(
            s"Cannot add zero-width output counter."
          )
        case _: TyUInt => ()
        case t =>
          throw new IllegalArgumentException(
            s"Variable provided for output counter has type $t."
              + " Expected an unsigned integer."
          )
      }
      val s =
        if (this.stm.equations.contains(inCtr))
          this.renameVar(inCtr)
        else
          this.stm
      val stmNextCalled = s.nextByVar(x)
      val next = Mux(
        stmNextCalled,
        Sum(C(1)(inCtr.typ), inCtr)(inCtr.typ),
        inCtr
      )(inCtr.typ)
      s.addAccumulator(inCtr, C(0)(inCtr.typ), next)
    }

    /** Add a new accumulator variable to this stream. <i>NOTE:</i> the new
      * variable may capture free variables in this stream.
      */
    def addAccumulator(x: Param, z: Expr, next: Expr): StmBuild = {
      val newEquations = this.stm.equations + (x -> (z, next))
      val isTyped = (x.hasType && z.hasType && next.hasType
        && (z.typ ~= x.typ) && (next.typ ~= x.typ))
      val t = if (isTyped) this.stm.typ else Missing
      StmBuild(this.stm.n, this.stm.data, this.stm.valid, newEquations)(
        t,
        annotations = this.stm.annotations
      )
    }

    /** Find the direct dependencies between accumulator variables in this
      * stream.
      */
    def accVarDependencies: DiGraph[Param] = {
      val edges = this.stm.nextByVar.toSeq
        .flatMap({ case (x, next) =>
          next.freeVars.intersect(this.stm.accVars).map(y => (x, y))
        })
        .toSet
      DiGraph(nodes = this.stm.accVars, edges = edges)
    }

    /** Find the accumulator variables that the output of this stream depends
      * on.
      */
    def outputDependencies: Set[Param] = {
      this.stm.data.freeVars
        .union(this.stm.valid.freeVars)
        .intersect(this.stm.accVars)
    }

    def annotate(annotation: StmBuildAnnotation): StmBuild = {
      StmBuild(
        this.stm.n,
        this.stm.data,
        this.stm.valid,
        this.stm.equations
      )(this.stm.typ, this.stm.annotations + annotation)
    }

    def annotateWithName(name: String): StmBuild = {
      val newAnnotations = this.stm.annotations
        .filter(!_.isInstanceOf[NameAnnotation])
        .+(NameAnnotation(name))
      StmBuild(
        this.stm.n,
        this.stm.data,
        this.stm.valid,
        this.stm.equations
      )(this.stm.typ, newAnnotations)
    }
  }
}
