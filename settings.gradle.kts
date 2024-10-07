rootProject.name = "VacManLighthouseProject"
include("src:main")
findProject(":src:main")?.name = "main"
