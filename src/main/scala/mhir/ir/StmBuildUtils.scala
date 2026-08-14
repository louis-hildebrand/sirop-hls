package mhir.ir

trait StmBuildUtils {

  /** Helper methods for [[StmBuild]].
    */
  implicit class StmBuildUtilsImplicit(stm: StmBuild) {

    def removeVarsExcept(keep: Set[Param]): StmBuild = {
      StmBuild(
        this.stm.n,
        this.stm.data,
        this.stm.valid,
        this.stm.accumulators.filter({ case (x, _) => keep.contains(x) }),
        this.stm.producers.filter({ case (x, _) => keep.contains(x) })
      )(annotations = this.stm.annotations)
    }

    /** Construct a new <code>StmBuild</code> that is equivalent to this one but
      * where all the accumulator and producer variables have been replaced by
      * fresh variables.
      */
    def renameVars: StmBuild = {
      this.stm.renameVars(
        this.stm.namesDefinedHere.map(x => x -> x.freshCopy).toMap
      )
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
        replacements.keys.forall(x => this.stm.namesDefinedHere.contains(x)),
        "all the variables to be replaced must appear in this stream"
      )
      val subs: Map[Expr, Expr] = replacements.toMap
      val newData = this.stm.data.subPreserveType(subs)
      val newValid = this.stm.valid.subPreserveType(subs)
      val newAccumulators =
        this.stm.accumulators.map({ case (x, (init, next)) =>
          val y =
            replacements.getOrElse(x, x).rebuild(x.typ).asInstanceOf[Param]
          y -> (init, next.subPreserveType(subs))
        })
      val newProducers = this.stm.producers.map({ case (x, (stm, ready)) =>
        val y = replacements.getOrElse(x, x).rebuild(x.typ).asInstanceOf[Param]
        y -> (stm, ready.subPreserveType(subs))
      })
      StmBuild(
        this.stm.n,
        newData,
        newValid,
        newAccumulators,
        newProducers
      )(this.stm.typ)
    }

    def replaceVars(replacements: Map[Param, Expr]): StmBuild = {
      // No canonicalization should be required here; accumulator variables
      // should not be part of the type of an expression
      implicit val c: Canonicalizer = NoOpCanonicalizer
      val invalidKeys =
        replacements.keys.filter(x =>
          !this.stm.accumulators.contains(x) && !this.stm.producers.contains(x)
        )
      if (invalidKeys.nonEmpty) {
        throw new IllegalArgumentException(
          s"Cannot replace variables ${invalidKeys.mkString(", ")} because they are neither accumulators nor producers."
            + s" The stream is $this."
        )
      } else {
        val subs: Map[Expr, Expr] = replacements.toMap
        StmBuild(
          this.stm.n,
          this.stm.data.subPreserveType(subs),
          this.stm.valid.subPreserveType(subs),
          this.stm.accumulators
            .filter({ case (x, _) => !replacements.contains(x) })
            .map({ case (x, (init, next)) =>
              x -> (init, next.subPreserveType(subs))
            }),
          this.stm.producers
            .filter({ case (x, _) => !replacements.contains(x) })
            .map({ case (x, (stm, ready)) =>
              x -> (stm, ready.subPreserveType(subs))
            })
        )(annotations = this.stm.annotations)
      }
    }

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
      val s = if (this.stm.namesDefinedHere.contains(outCtr)) {
        this.renameVar(outCtr)
      } else {
        this.stm
      }
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
      val s = if (this.stm.namesDefinedHere.contains(inCtr)) {
        this.renameVar(inCtr)
      } else {
        this.stm
      }
      val (_, ready) = s.producers(x)
      val next = Mux(
        ready,
        Sum(C(1)(inCtr.typ), inCtr)(inCtr.typ),
        inCtr
      )(inCtr.typ)
      s.addAccumulator(inCtr, C(0)(inCtr.typ), next)
    }

    /** Add a new accumulator variable to this stream. <i>NOTE:</i> the new
      * variable may capture free variables in this stream.
      */
    def addAccumulator(x: Param, z: Expr, next: Expr): StmBuild = {
      require(x.typ.isData)
      val newAccumulators = this.stm.accumulators + (x -> (z, next))
      val isTyped = (x.hasType && z.hasType && next.hasType
        && (z.typ ~= x.typ) && (next.typ ~= x.typ))
      val t = if (isTyped) this.stm.typ else Missing
      StmBuild(
        this.stm.n,
        this.stm.data,
        this.stm.valid,
        newAccumulators,
        this.stm.producers
      )(t, annotations = this.stm.annotations)
    }

    def mapProducers(
        f: (Param, (Expr, Expr)) => (Param, (Expr, Expr))
    ): StmBuild = {
      StmBuild(
        this.stm.n,
        this.stm.data,
        this.stm.valid,
        this.stm.accumulators,
        this.stm.producers.map(f.tupled)
      )(annotations = this.stm.annotations)
    }

    /** Find the direct dependencies between variables defined in this
      * [[StmBuild]].
      */
    def internalDependencies: DiGraph[Param] = {
      val edges = (this.stm.accumulators ++ this.stm.producers).toSeq
        .flatMap({ case (x, (_, next)) =>
          next.freeVars.intersect(this.stm.namesDefinedHere).map(x -> _)
        })
        .toSet
      DiGraph(nodes = this.stm.namesDefinedHere, edges = edges)
    }

    /** Find the accumulator variables that the output of this stream depends
      * on.
      */
    def outputDependencies: Set[Param] = {
      this.stm.data.freeVars
        .union(this.stm.valid.freeVars)
        .intersect(this.stm.namesDefinedHere)
    }

    def annotate(annotation: StmBuildAnnotation): StmBuild = {
      StmBuild(
        this.stm.n,
        this.stm.data,
        this.stm.valid,
        this.stm.accumulators,
        this.stm.producers
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
        this.stm.accumulators,
        this.stm.producers
      )(this.stm.typ, newAnnotations)
    }

    def nameAnnotation: Option[String] = {
      this.stm.annotations.collectFirst({ case NameAnnotation(name) => name })
    }
  }
}
