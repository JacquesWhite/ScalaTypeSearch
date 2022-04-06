//package org.jetbrains.plugins.typeSearch
//
//import com.intellij.ide.ui.search.{DefaultSearchEverywhereTabsCustomization, SearchEverywhereTabsCustomization}
//
//import java.util
//
//class TypeSearchEverywhereTabsCustomization  extends SearchEverywhereTabsCustomization {
//  override def getContributorsWithTab: util.List[String] = {
//    val customList: util.List[String] = (new DefaultSearchEverywhereTabsCustomization).getContributorsWithTab()
//    customList.add("TypeSearchContributor")
//    println(customList)
//    customList
//  }
//}
//
