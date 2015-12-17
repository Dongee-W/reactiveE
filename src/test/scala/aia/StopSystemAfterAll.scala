package aia

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

/**
 * Created by bradleywu on 2015/10/23.
 */
trait StopSystemAfterAll extends BeforeAndAfterAll {
  //<co id="ch02-stop-system-before-and-after-all"/>
  this: TestKit with Suite => //<co id="ch02-stop-system-self-type"/>
  override protected def afterAll() {
    super.afterAll()
    system.terminate() //<co id="ch02-stop-system-shutdown"/>
  }
}