package mhir.gen

sealed trait TestResult
case object TestPassed extends TestResult
case object MissingVcom extends TestResult
case object DesignCompileFailed extends TestResult
case object MissingVsim extends TestResult
case object TestbenchCompileFailed extends TestResult
case object SimulationFailed extends TestResult
case object SimulationTimeout extends TestResult
case object NoTests extends TestResult
case object UnknownFailure extends TestResult
