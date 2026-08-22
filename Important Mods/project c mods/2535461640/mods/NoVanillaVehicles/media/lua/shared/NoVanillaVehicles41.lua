

if VehicleZoneDistribution then -- check if the table exists for backwards compatibility
	
-- Devs cars

VehicleZoneDistribution.parkingstall.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.SmallCar02"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.CarTaxi"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.CarTaxi2"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.PickUpTruck"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.PickUpVan"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.VanSeats"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.Van"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.StepVan"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.ModernCar"] = nil;
VehicleZoneDistribution.parkingstall.vehicles["Base.ModernCar02"] = nil;

-- Trailer Parks, have a chance to spawn burnt cars, some on top of each others, it's like a pile of junk cars

VehicleZoneDistribution.trailerpark.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.trailerpark.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.trailerpark.vehicles["Base.SmallCar02"] = nil;
VehicleZoneDistribution.trailerpark.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.trailerpark.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.trailerpark.vehicles["Base.StepVan"] = nil;

-- bad vehicles, moslty used in poor area, sometimes around pub etc.

VehicleZoneDistribution.bad.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.SmallCar02"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.StepVan"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.Van"] = nil;

-- medium vehicles, used in some of the good looking area, or in suburbs

VehicleZoneDistribution.medium.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.PickUpTruck"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.PickUpVan"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.VanSeats"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.Van"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.StepVan"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.VanSeats"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.SUV"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.OffRoad"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.ModernCar"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.ModernCar02"] = nil;
VehicleZoneDistribution.medium.vehicles["Base.CarLuxury"] = nil;


-- good vehicles, used in good looking area, they're meant to spawn only good cars, so they're on every good looking house.

VehicleZoneDistribution.good.vehicles["Base.ModernCar"]= nil;
VehicleZoneDistribution.good.vehicles["Base.ModernCar02"]= nil;
VehicleZoneDistribution.good.vehicles["Base.SUV"] = nil;
VehicleZoneDistribution.good.vehicles["Base.OffRoad"] = nil;
VehicleZoneDistribution.good.vehicles["Base.CarLuxury"] = nil;
VehicleZoneDistribution.good.vehicles["Base.SportsCar"] = nil;

-- sports vehicles, sometimes on good looking area.

VehicleZoneDistribution.sport.vehicles["Base.CarLuxury"] = nil;
VehicleZoneDistribution.sport.vehicles["Base.SportsCar"] = nil;


-- junkyard, spawn damaged & burnt vehicles, less chance of finding keys but more cars.
-- also used for the random car crash.

VehicleZoneDistribution.junkyard.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.SmallCar02"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.CarTaxi"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.CarTaxi2"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.PickUpTruck"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.PickUpVan"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.VanSeats"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.Van"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.StepVan"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.ModernCar"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.ModernCar02"] = nil;

	-- Traffic Jam
local trafficjamVehicles = {};
trafficjamVehicles["Base.CarNormal"] = nil;
trafficjamVehicles["Base.SmallCar"] = nil;
trafficjamVehicles["Base.SmallCar02"] = nil;
trafficjamVehicles["Base.CarTaxi"] = nil;
trafficjamVehicles["Base.CarTaxi2"] = nil;
trafficjamVehicles["Base.PickUpTruck"] = nil;
trafficjamVehicles["Base.PickUpVan"] = nil;
trafficjamVehicles["Base.CarStationWagon"] = nil;
trafficjamVehicles["Base.CarStationWagon2"] = nil;
trafficjamVehicles["Base.VanSeats"] = nil;
trafficjamVehicles["Base.Van"] = nil;
trafficjamVehicles["Base.StepVan"] = nil;
trafficjamVehicles["Base.ModernCar"] = nil;
trafficjamVehicles["Base.ModernCar02"] = nil;

VehicleZoneDistribution.trafficjamw = {};
VehicleZoneDistribution.trafficjamw.vehicles = trafficjamVehicles;
VehicleZoneDistribution.trafficjamw.chanceToSpawnBurnt = 80;
VehicleZoneDistribution.trafficjamw.baseVehicleQuality = 0.3;
VehicleZoneDistribution.trafficjamw.chanceToPartDamage = 80;
VehicleZoneDistribution.trafficjamw.chanceToSpawnKey = 20;

VehicleZoneDistribution.trafficjame = {};
VehicleZoneDistribution.trafficjame.vehicles = trafficjamVehicles;
VehicleZoneDistribution.trafficjame.chanceToSpawnBurnt = 80;
VehicleZoneDistribution.trafficjame.baseVehicleQuality = 0.3;
VehicleZoneDistribution.trafficjame.chanceToPartDamage = 80;
VehicleZoneDistribution.trafficjame.chanceToSpawnKey = 20;

VehicleZoneDistribution.trafficjamn = {};
VehicleZoneDistribution.trafficjamn.vehicles = trafficjamVehicles;
VehicleZoneDistribution.trafficjamn.chanceToSpawnBurnt = 80;
VehicleZoneDistribution.trafficjamn.baseVehicleQuality = 0.3;
VehicleZoneDistribution.trafficjamn.chanceToPartDamage = 80;
VehicleZoneDistribution.trafficjamn.chanceToSpawnKey = 20;

VehicleZoneDistribution.trafficjams = {};
VehicleZoneDistribution.trafficjams.vehicles = trafficjamVehicles;
VehicleZoneDistribution.trafficjams.chanceToSpawnBurnt = 80;
VehicleZoneDistribution.trafficjams.baseVehicleQuality = 0.3;
VehicleZoneDistribution.trafficjams.chanceToPartDamage = 80;
VehicleZoneDistribution.trafficjams.chanceToSpawnKey = 20;

-- ****************************** --
--          SPECIAL VEHICLES      --
-- ****************************** --

-- police

VehicleZoneDistribution.police.vehicles["Base.PickUpVanLightsPolice"] = nil;
VehicleZoneDistribution.police.vehicles["Base.CarLightsPolice"] = nil;

-- fire dept

VehicleZoneDistribution.fire.vehicles["Base.PickUpVanLightsFire"] = nil;
VehicleZoneDistribution.fire.vehicles["Base.PickUpTruckLightsFire"] = nil;

-- ranger

VehicleZoneDistribution.ranger.vehicles["Base.CarLightsRanger"]= nil;
VehicleZoneDistribution.ranger.vehicles["Base.PickUpVanLightsRanger"] = nil;
VehicleZoneDistribution.ranger.vehicles["PickUpTruckLightsRanger"] = nil;


-- mccoy

VehicleZoneDistribution.mccoy.vehicles["Base.PickUpVanMccoy"] = nil;
VehicleZoneDistribution.mccoy.vehicles["Base.PickUpTruckMccoy"] = nil;
VehicleZoneDistribution.mccoy.vehicles["Base.VanMccoy"] = nil;

-- postal (mail)

VehicleZoneDistribution.postal.vehicles["Base.StepVanMail"] = nil;
VehicleZoneDistribution.postal.vehicles["Base.VanSpecial"] = nil;

-- spiffo

VehicleZoneDistribution.spiffo.vehicles["Base.VanSpiffo"] = nil;

-- ambulance

VehicleZoneDistribution.ambulance.vehicles["Base.VanAmbulance"] = nil;

-- radio

VehicleZoneDistribution.radio.vehicles["Base.VanRadio"] = nil;

-- fossoil

VehicleZoneDistribution.fossoil.vehicles["Base.PickUpVanLightsFossoil"] = nil;
VehicleZoneDistribution.fossoil.vehicles["Base.PickUpTruckLightsFossoil"] = nil;
VehicleZoneDistribution.fossoil.vehicles["Base.VanSpecial"] = nil;

-- scarlet dist

VehicleZoneDistribution.scarlet.vehicles["Base.StepVan_Scarlet"] = nil;

-- mass genfac co.

VehicleZoneDistribution.massgenfac.vehicles["Base.Van_MassGenFac"] = nil;

-- transit

VehicleZoneDistribution.transit.vehicles["Base.Van_Transit"] = nil;

-- 3Network

VehicleZoneDistribution.network3.vehicles["Base.VanRadio_3N"] = nil;

-- KY Heralds

VehicleZoneDistribution.kyheralds.vehicles["Base.StepVan_Heralds"] = nil;

-- LectroMax

VehicleZoneDistribution.lectromax.vehicles["Base.Van_LectroMax"] = nil;

-- Knox Distillery

VehicleZoneDistribution.knoxdisti.vehicles["Base.Van_KnoxDisti"] = nil;


end