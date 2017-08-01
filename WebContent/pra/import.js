	angular.module("myapp",['ngRoute'])
	.config(['$routeProvider',function($routeProvider){
		$routeProvider
		.when('/table',{controller:tableContr,templateUrl:'ceshi.html'})
		.otherwise({templateUrl:'ceshi.html'})
	}]);
	function tableContr($scope,$http){
		$http({
			url:'http://localhost:8080/zjsun_zspring_mvc/1.json',
			method:'get'
		}).then(function successCallback(response){
			$scope.message = response.data.records;
		},function errorCallback(){alert("error")});
	}