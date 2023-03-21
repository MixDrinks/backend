package org.mixdrinks.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TransliterationKtTest : FunSpec({

    listOf(
        "Борщагівка" to "Borshchahivka",
        "Борисенко" to "Borysenko",
        "Вінниця" to "Vinnytsia",
        "Володимир" to "Volodymyr",
        "Гадяч" to "Hadiach",
        "Богдан" to "Bohdan",
        "Згурський" to "Zghurskyi",
        "Ґалаґан" to "Galagan",
        "Ґорґани" to "Gorgany",
        "Донецьк" to "Donetsk",
        "Дмитро" to "Dmytro",
        "Рівне" to "Rivne",
        "Олег" to "Oleh",
        "Есмань" to "Esman",
        "Єнакієве" to "Yenakiieve",
        "Гаєвич" to "Haievych",
        "Короп'є" to "Koropie",
        "Житомир" to "Zhytomyr",
        "Жанна" to "Zhanna",
        "Жежелів" to "Zhezheliv",
        "Закарпаття" to "Zakarpattia",
        "Казимирчук" to "Kazymyrchuk",
        "Медвин" to "Medvyn",
        "Михайленко" to "Mykhailenko",
        "Іванків" to "Ivankiv",
        "Іващенко" to "Ivashchenko",
        "Їжакевич" to "Yizhakevych",
        "Кадиївка" to "Kadyivka",
        "Мар'їне" to "Marine",
        "Йосипівка" to "Yosypivka",
        "Стрий" to "Stryi",
        "Олексій" to "Oleksii",
        "Київ" to "Kyiv",
        "Коваленко" to "Kovalenko",
        "Лебедин" to "Lebedyn",
        "Леонід" to "Leonid",
        "Миколаїв" to "Mykolaiv",
        "Маринич" to "Marynych",
        "Ніжин" to "Nizhyn",
        "Наталія" to "Nataliia",
        "Одеса" to "Odesa",
        "Онищенко" to "Onyshchenko",
        "Полтава" to "Poltava",
        "Петро" to "Petro",
        "Решетилівка" to "Reshetylivka",
        "Рибчинський" to "Rybchynskyi",
        "Суми" to "Sumy",
        "Соломія" to "Solomiia",
        "Тернопіль" to "Ternopil",
        "Троць" to "Trots",
        "Ужгород" to "Uzhhorod",
        "Уляна" to "Uliana",
        "Фастів" to "Fastiv",
        "Філіпчук" to "Filipchuk",
        "Харків" to "Kharkiv",
        "Христина" to "Khrystyna",
        "Біла Церква" to "Bila Tserkva",
        "Стеценко" to "Stetsenko",
        "Чернівці" to "Chernivtsi",
        "Шевченко" to "Shevchenko",
        "Шостка" to "Shostka",
        "Кишеньки" to "Kyshenky",
        "Щербухи" to "Shcherbukhy",
        "Гоща" to "Hoshcha",
        "Гаращенко" to "Harashchenko",
        "Юрій" to "Yurii",
        "Корюківка" to "Koriukivka",
        "Яготин" to "Yahotyn",
        "Ярошенко" to "Yaroshenko",
        "Костянтин" to "Kostiantyn",
        "Знам'янка" to "Znamianka",
        "Феодосія" to "Feodosiia",
        "Гусятин" to "Husiatyn",
        "пеніцелін" to "penitselin",
        "Згорани" to "Zghorany",
        "Розгон" to "Rozghon",
        "Скритна леді" to "Skrytna ledi",
        "Яготин Ярошенко" to "Yahotyn Yaroshenko",
    ).forEach { (ukraine, latin) ->
        test("Transliteration of $ukraine is $latin") {
            translit(ukraine) shouldBe latin
        }
    }

    listOf(
        "Шен Пуер" to "shen-puer",
        "Вид пуеру" to "vyd-pueru",
        "Смола Пуеру (Ча Гао)" to "smola-pueru-cha-hao",
        "Мініточа (таблетки)" to "minitocha-tabletky",
        "Пробники" to "probnyky",
        "Підбірки" to "pidbirky",
    ).forEach {(ukraine, slug) ->
        test("Slug of $ukraine is $slug") {
            slug(ukraine) shouldBe slug
        }
    }
})

