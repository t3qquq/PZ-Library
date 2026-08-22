

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
VehicleZoneDistribution.trailerpark.vehicles["Base.PickUpTruck_Camo"] = nil;
VehicleZoneDistribution.trailerpark.vehicles["Base.PickUpVan_Camo"] = nil;

-- bad vehicles, moslty used in poor area, sometimes around pub etc.

VehicleZoneDistribution.bad.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.SmallCar02"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.StepVan"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.Van"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.PickUpTruck_Camo"] = nil;
VehicleZoneDistribution.bad.vehicles["Base.PickUpVan_Camo"] = nil;


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
VehicleZoneDistribution.medium.vehicles["Base.SUV"] = {index = -1, spawnChance = 0};
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

-- cardealships etc

VehicleZoneDistribution.luxuryDealership.vehicles["Base.ModernCar"] = nil;
VehicleZoneDistribution.luxuryDealership.vehicles["Base.ModernCar02"] = nil;
VehicleZoneDistribution.luxuryDealership.vehicles["Base.SUV"] = nil;
VehicleZoneDistribution.luxuryDealership.vehicles["Base.OffRoad"] = nil;
VehicleZoneDistribution.luxuryDealership.vehicles["Base.CarLuxury"] = nil;
VehicleZoneDistribution.luxuryDealership.vehicles["Base.SportsCar"] = nil;


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
VehicleZoneDistribution.junkyard.vehicles["Base.PickUpTruck_Camo"] = nil;
VehicleZoneDistribution.junkyard.vehicles["Base.PickUpVan_Camo"] = nil;
VehicleZoneDistribution.junkyard.chanceToSpawnBurnt = 20;

	-- Traffic Jam
VehicleZoneDistribution.trafficjamw.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.SmallCar02"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.CarTaxi"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.CarTaxi2"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.PickUpTruck"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.PickUpVan"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.VanSeats"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.Van"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.StepVan"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.ModernCar"] = nil;
VehicleZoneDistribution.trafficjamw.vehicles["Base.ModernCar02"] = nil;
VehicleZoneDistribution.trafficjamw.chanceToSpawnBurnt = 40;

-- ****************************** --
--          SPECIAL VEHICLES      --
-- ****************************** --

-- police

VehicleZoneDistribution.police.vehicles["Base.PickUpVanLightsPolice"] = nil;
VehicleZoneDistribution.police.vehicles["Base.CarLightsPolice"] = nil;
VehicleZoneDistribution.police.vehicles["Base.VanSeats_Prison"] = nil;


-- prison

VehicleZoneDistribution.prison.vehicles["Base.PickUpVanLightsPolice"] = nil;
VehicleZoneDistribution.prison.vehicles["Base.CarLightsPolice"] = nil;
VehicleZoneDistribution.prison.vehicles["Base.VanSeats_Prison"] = nil;


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
-- carpenter

VehicleZoneDistribution.carpenter.vehicles["Base.PickUpVanLightsCarpenter"] = nil;
VehicleZoneDistribution.carpenter.vehicles["Base.VanCarpenter"] = nil;


-- postal (mail)

VehicleZoneDistribution.postal.vehicles["Base.StepVanMail"] = nil;
VehicleZoneDistribution.postal.vehicles["Base.VanMail"] = nil;

-- spiffo

VehicleZoneDistribution.spiffo.vehicles["Base.VanSpiffo"] = nil;
VehicleZoneDistribution.spiffo.vehicles["Base.TrailerAdvert"] = nil;

-- ambulance

VehicleZoneDistribution.ambulance.vehicles["Base.VanAmbulance"] = nil;

-- radio

VehicleZoneDistribution.radio.vehicles["Base.VanRadio"] = nil;
VehicleZoneDistribution.radio.vehicles["Base.TrailerAdvert"] = nil;

-- fossoil

VehicleZoneDistribution.fossoil.vehicles["Base.PickUpVanLightsFossoil"] = nil;
VehicleZoneDistribution.fossoil.vehicles["Base.PickUpTruckLightsFossoil"] = nil;

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

-- advertising

VehicleZoneDistribution.advertising.vehicles["Base.TrailerAdvert"] = nil;

-- airport shuttle for both the airport and also the havisham hotel

VehicleZoneDistribution.airportshuttle.vehicles["Base.VanSeatsAirportShuttle"] = nil;
VehicleZoneDistribution.airportshuttle.vehicles["Base.CarTaxi"] = nil;
VehicleZoneDistribution.airportshuttle.vehicles["Base.CarTaxi2"] = nil;

-- airport service vehicles

VehicleZoneDistribution.airportservice.vehicles["Base.Base.PickUpTruckLightsAirport"] = nil;
VehicleZoneDistribution.airportservice.vehicles["Base.PickUpTruckLightsAirportSecurity"] = nil;
VehicleZoneDistribution.airportservice.vehicles["Base.StepVanAirportCatering"] = nil;
VehicleZoneDistribution.airportservice.vehicles["Base.VanSeatsAirportShuttle"] = nil;
VehicleZoneDistribution.airportservice.vehicles["Base.VanMeltingPointMetal"] = nil;
VehicleZoneDistribution.airportservice.vehicles["Base.VanMobileMechanics"] = nil;

-- farms

VehicleZoneDistribution.farm.vehicles["Base.PickUpTruck"] = nil;
VehicleZoneDistribution.farm.vehicles["Base.PickUpVan"] = nil;
VehicleZoneDistribution.farm.vehicles["Base.Trailer"] = {index = -1, spawnChance = 10};
VehicleZoneDistribution.farm.vehicles["Base.TrailerCover"] = {index = -1, spawnChance = 10};
VehicleZoneDistribution.farm.vehicles["Base.Trailer_Livestock"] = {index = -1, spawnChance = 50};
VehicleZoneDistribution.farm.vehicles["Base.PickUpTruck_Camo"] = nil;
VehicleZoneDistribution.farm.vehicles["Base.PickUpVan_Camo"] = nil;

-- the following are special tables used for randomized stories, and not used for regular map-based vehicle spawning

-- vehicles for bluecollar manual trades workers

VehicleZoneDistribution.trades.vehicles["Base.Van"] = nil;
VehicleZoneDistribution.trades.vehicles["Base.StepVan"] = nil;
VehicleZoneDistribution.trades.vehicles["Base.PickUpTruck"] = nil;
VehicleZoneDistribution.trades.vehicles["Base.PickUpVan"] = nil;
VehicleZoneDistribution.trades.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.trades.vehicles["Base.StepVan"] = nil;

-- delivery workers including caterers

VehicleZoneDistribution.delivery.vehicles["Base.Van"] = nil;
VehicleZoneDistribution.delivery.vehicles["Base.StepVan"] = nil;

-- vehicles for well-off white collar workers

VehicleZoneDistribution.professional.vehicles["Base.ModernCar"] = nil;
VehicleZoneDistribution.professional.vehicles["Base.ModernCar02"] = nil;
VehicleZoneDistribution.professional.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.professional.vehicles["Base.CarLuxury"] = nil;
VehicleZoneDistribution.professional.vehicles["Base.SUV"] = nil;

-- vehicles for middle-classe workers

VehicleZoneDistribution.middleClass.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.middleClass.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.middleClass.vehicles["Base.SmallCar02"] = nil;
VehicleZoneDistribution.middleClass.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.middleClass.vehicles["Base.CarStationWagon2"] = nil;

-- vehicles for lower income workers and student

VehicleZoneDistribution.struggling.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.struggling.vehicles["Base.SmallCar"] = nil;
VehicleZoneDistribution.struggling.vehicles["Base.SmallCar02"] = nil;

-- for special vehicle stories

VehicleZoneDistribution.evacuee.vehicles["Base.CarNormal"] = nil;
VehicleZoneDistribution.evacuee.vehicles["Base.CarStationWagon"] = nil;
VehicleZoneDistribution.evacuee.vehicles["Base.CarStationWagon2"] = nil;
VehicleZoneDistribution.evacuee.vehicles["Base.SUV"] = nil;



end