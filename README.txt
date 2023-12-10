Pasta do Projecto de Seguranca Informatica

Foi desenvolvido o trabalho da cadeira de Seguranca Informatica: Criacao de uma biblioteca de protecao de aplicacoes.

Neste projecto desenvolvemos a biblioteca de protecao de aplicacoes -> MRLicensing,
aplicamos essa biblioteca a uma aplicacao -> JogoDoGalo e
criamos o gestor de licencas -> LicenseManager.

As pastas MRLicensing, JogoDoGalo e LicenseManager correspondem ao codigo fonte dos projectos

A pasta distributionUser é apenas o que um utilizador normal que quer jogar o JogoDoGalo vai receber e corresponde ao dist do projecto JogoDoGalo

A pasta distributionAppDeveloper é apenas o que um App Developer necessita para implementar a biblioteca na sua aplicacao 
(contendo as pastas MRLicensing e LicenseManager que sao as pastas dist dos projetos correspondentes),
em que a pasta distributionAppDeveloper/MRLicensing vai ter de ser incorporada no projecto que esta a desenvolver,
seguindo as instrucoes do distributionAppDeveloper/MRLicensing/README.txt,
e em que o jar file distributionAppDeveloper/LicenseManager/LicenseManager.jar sera usado pelo App Developer
para gerir as apps que implementam a biblioteca e gerar novas licencas para os Utilizadores.