package org.jetbrains.plugins.typeSearch

import com.intellij.ide.actions.searcheverywhere.{FoundItemDescriptor, SearchEverywhereContributor, SearchEverywhereContributorFactory, WeightedSearchEverywhereContributor}
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.{Processor, ThrowableRunnable}
import com.intellij.util.SlowOperations.allowSlowOperations
import org.jetbrains.plugins.scala.caches.ScalaShortNamesCacheManager
import TypeSearchContributor.inkuireService
import org.virtuslab.inkuire.engine.common.model.ExternalSignature
import org.virtuslab.inkuire.engine.common.service.ScalaExternalSignaturePrettifier
import com.intellij.openapi.application.ApplicationManager

import java.io.File
import javax.swing.ListCellRenderer
import scala.language.postfixOps
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class StdFunctionRef(val externalSignature: ExternalSignature) {
  val getName: String = externalSignature.name
  val getFQName: String = externalSignature.packageName + "#" + getName
  val getPrettyName: String = externalSignature.name.split("\\$").apply(0)
  val getPrettyFQName: String = externalSignature.packageName + "#" + getPrettyName
  val getPrettyContext: String = (new ScalaExternalSignaturePrettifier).prettify(externalSignature)
}


object TypeSearchContributor {

  val file = new File("./resources")
  val inkuireService: Future[InkuireService] = Future {
    new InkuireService(file.toURI.toURL.toString)
  }
  var project: Option[Project] = None

  class Factory extends SearchEverywhereContributorFactory[AnyRef] {
    override def createContributor(initEvent: AnActionEvent): SearchEverywhereContributor[AnyRef] = {
      project = Some(initEvent.getProject)
      (new TypeSearchContributor).asInstanceOf[SearchEverywhereContributor[AnyRef]]
    }
  }
}

class TypeSearchContributor extends WeightedSearchEverywhereContributor[PsiMethod] {
  val cellRenderer = new TypeSearchListCellRenderer

  override def getElementsRenderer: ListCellRenderer[_ >: Any] =
    (new TypeSearchListCellRenderer).asInstanceOf[ListCellRenderer[_ >: Any]]

  override def getSearchProviderId: String = getClass.getSimpleName

  override def getGroupName: String = "Type Search"

  override def getSortWeight = 1000

  // default is false
  override def isShownInSeparateTab: Boolean = true

  // default is true
  override def showInFindResults(): Boolean = true

  // Simple accuracy measure
  def calculateWeightOfMatch(pattern: String, element: StdFunctionRef): Int = {
    val patternParameters = pattern.split("=>").map(s => s.trim)
    val prettifier = new ScalaExternalSignaturePrettifier
    val elementParametersStringified = prettifier.prettify(element.externalSignature)

    val weight = {
      if (elementParametersStringified == pattern) {
        100
      }
      else {
        var ctr = 0
        for (parameter <- patternParameters) {
          val isMatch = if (elementParametersStringified.contains(parameter)) 1 else 0
          ctr += isMatch
        }
        ctr
      }
    }

    weight
  }

  private def findPSIForResult(resultRef: StdFunctionRef): PsiMethod = {
    val FQClassName: String = resultRef.externalSignature.packageName
    val FQName: String = resultRef.externalSignature.name.split("\\$").apply(0)

    def findMethodByFQN(scalaShortNamesCacheManager: ScalaShortNamesCacheManager,
                        projectWithLibrariesScope: GlobalSearchScope): Option[PsiMethod] = {
      try {
        scalaShortNamesCacheManager
          .getClassByFQName(FQClassName, projectWithLibrariesScope)
          .getAllMethods
          .find(_.getName == FQName)
      }
      catch {
        case _: NullPointerException => None
      }
    }

    val projectEvent = TypeSearchContributor.project.get
    val scalaShortNamesCacheManager = ScalaShortNamesCacheManager.getInstance(projectEvent)
    val otherProjectScope = GlobalSearchScope.allScope(projectEvent)

    findMethodByFQN(scalaShortNamesCacheManager, otherProjectScope).orNull
  }

  override def fetchWeightedElements(pattern: String, progressIndicator: ProgressIndicator,
                                     consumer: Processor[_ >: FoundItemDescriptor[PsiMethod]]): Unit = {
    val results = Await.result(inkuireService, Duration.Inf).query(pattern)
    for (result <- results) {
      class MyRunnable extends Runnable {
        override def run(): Unit = {
          val resultRef: StdFunctionRef = new StdFunctionRef(result)
          val weight = calculateWeightOfMatch(pattern, resultRef)
          val psiMethod = findPSIForResult(resultRef)
          val itemDescriptor = new FoundItemDescriptor[PsiMethod](psiMethod, weight)

          consumer.process(itemDescriptor)
        }
      }

      ApplicationManager.getApplication.runReadAction(new MyRunnable)
    }
  }

  override def processSelectedItem(selected: PsiMethod, modifiers: Int, searchText: String): Boolean = {
    class MyThread extends ThrowableRunnable[Throwable] {
      override def run(): Unit = {
        selected match {
          case null => println("psiMethodToNavigate is null")
          case _ => selected.navigate(true)
        }
      }
    }
    allowSlowOperations(new MyThread)
    true // close SEWindow
  }

  override def getDataForItem(element: PsiMethod, dataId: String): Option[Any] = null
}