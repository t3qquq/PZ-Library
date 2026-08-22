-- Smoking Sounds Overhaul -- asset library (generated).

SmokingSoundsOverhaul = SmokingSoundsOverhaul or {}
SmokingSoundsOverhaul.Lib = {
	closes = {
		lighter = {
			{
				d = 0.33,
				n = 1,
				s = "Smoking_close_lighter1",
			},
			{
				d = 0.47,
				n = 2,
				s = "Smoking_close_lighter2",
			},
			{
				d = 0.34,
				n = 3,
				s = "Smoking_close_lighter3",
			},
		},
	},
	draws = {
		f = {
			{
				d = 1.4,
				s = "Smoking_draw1f",
			},
			{
				d = 2.4,
				s = "Smoking_draw2f",
			},
			{
				d = 1.55,
				s = "Smoking_draw3f",
			},
			{
				d = 1.82,
				s = "Smoking_draw4f",
			},
			{
				d = 2.38,
				s = "Smoking_draw5f",
			},
			{
				d = 1.1,
				s = "Smoking_draw6f",
			},
			{
				d = 1.2,
				s = "Smoking_draw7f",
			},
			{
				d = 3.25,
				s = "Smoking_draw8f",
			},
			{
				d = 2.56,
				s = "Smoking_draw9f",
			},
		},
		m = {
			{
				d = 1.8,
				s = "Smoking_draw1m",
			},
			{
				d = 2.5,
				s = "Smoking_draw2m",
			},
			{
				d = 1.52,
				s = "Smoking_draw3m",
			},
			{
				d = 1.45,
				s = "Smoking_draw4m",
			},
			{
				d = 1.8,
				s = "Smoking_draw5m",
			},
			{
				d = 2.46,
				s = "Smoking_draw6m",
			},
			{
				d = 1.2,
				s = "Smoking_draw7m",
			},
			{
				d = 1.4,
				s = "Smoking_draw8m",
			},
			{
				d = 2.87,
				s = "Smoking_draw9m",
			},
			{
				d = 2.69,
				s = "Smoking_draw10m",
			},
		},
	},
	exhales = {
		f = {
			{
				d = 1.8,
				s = "Smoking_exh1f",
			},
			{
				d = 1.75,
				s = "Smoking_exh2f",
			},
			{
				d = 1.49,
				s = "Smoking_exh3f",
			},
			{
				d = 1.4,
				s = "Smoking_exh4f",
			},
			{
				d = 1.6,
				s = "Smoking_exh5f",
			},
			{
				d = 1.23,
				s = "Smoking_exh6f",
			},
		},
		m = {
			{
				d = 1.45,
				s = "Smoking_exh1m",
			},
			{
				d = 1.45,
				s = "Smoking_exh2m",
			},
			{
				d = 1.65,
				s = "Smoking_exh3m",
			},
			{
				d = 0.9,
				s = "Smoking_exh4m",
			},
			{
				d = 1.4,
				s = "Smoking_exh5m",
			},
			{
				d = 1.52,
				s = "Smoking_exh6m",
			},
		},
	},
	legacy = {
		lighter = {
			f = {
				"Smoking_lighter1f",
				"Smoking_lighter2f",
				"Smoking_lighter3f",
			},
			m = {
				"Smoking_lighter1m",
				"Smoking_lighter2m",
				"Smoking_lighter3m",
			},
		},
		matches = {
			f = {
				"Smoking_matches1f",
				"Smoking_matches2f",
				"Smoking_matches3f",
			},
			m = {
				"Smoking_matches1m",
				"Smoking_matches2m",
				"Smoking_matches3m",
			},
		},
	},
	opens = {
		lighter = {
			{
				d = 0.34,
				n = 1,
				s = "Smoking_open_lighter1",
			},
			{
				d = 0.3,
				n = 2,
				s = "Smoking_open_lighter2",
			},
			{
				d = 0.34,
				n = 3,
				s = "Smoking_open_lighter3",
			},
		},
		matches = {
			{
				d = 0.74,
				n = 1,
				s = "Smoking_open_matches1",
			},
			{
				d = 0.74,
				n = 2,
				s = "Smoking_open_matches2",
			},
			{
				d = 0.7,
				n = 3,
				s = "Smoking_open_matches3",
			},
		},
	},
	strikeSeqs = {
		lighter = {
			{
				closeAfter = 2.6,
				files = {
					{
						d = 0.23,
						s = "Smoking_strike_lighter1_1",
					},
					{
						d = 0.42,
						s = "Smoking_strike_lighter1_2",
					},
				},
				firstOff = 0.43,
				flicks = 2,
				g = nil,
				gaps = {
					0.6,
				},
				id = "lighter1",
				n = 1,
			},
			{
				closeAfter = 2.53,
				files = {
					{
						d = 0.21,
						s = "Smoking_strike_lighter2_1",
					},
					{
						d = 0.34,
						s = "Smoking_strike_lighter2_2",
					},
				},
				firstOff = 0.43,
				flicks = 2,
				g = nil,
				gaps = {
					0.32,
				},
				id = "lighter2",
				n = 2,
			},
			{
				closeAfter = 2.66,
				files = {
					{
						d = 0.21,
						s = "Smoking_strike_lighter3_1",
					},
					{
						d = 0.21,
						s = "Smoking_strike_lighter3_2",
					},
					{
						d = 0.36,
						s = "Smoking_strike_lighter3_3",
					},
				},
				firstOff = 0.43,
				flicks = 3,
				g = nil,
				gaps = {
					0.4,
					0.4,
				},
				id = "lighter3",
				n = 3,
			},
		},
		matches = {
			{
				closeAfter = nil,
				files = {
					{
						d = 1.48,
						s = "Smoking_strike_matches1_1",
					},
				},
				firstOff = 0.87,
				flicks = 2,
				g = nil,
				gaps = {},
				id = "matches1",
				n = 1,
			},
			{
				closeAfter = nil,
				files = {
					{
						d = 1.34,
						s = "Smoking_strike_matches2m_1",
					},
				},
				firstOff = 0.69,
				flicks = 1,
				g = nil,
				gaps = {},
				id = "matches2m",
				n = 2,
			},
			{
				closeAfter = nil,
				files = {
					{
						d = 1.4,
						s = "Smoking_strike_matches2f_1",
					},
				},
				firstOff = 0.73,
				flicks = 1,
				g = nil,
				gaps = {},
				id = "matches2f",
				n = 2,
			},
			{
				closeAfter = nil,
				files = {
					{
						d = 0.23,
						s = "Smoking_strike_matches3m_1",
					},
					{
						d = 0.21,
						s = "Smoking_strike_matches3m_2",
					},
					{
						d = 1.13,
						s = "Smoking_strike_matches3m_3",
					},
				},
				firstOff = 0.59,
				flicks = 3,
				g = nil,
				gaps = {
					0.72,
					0.88,
				},
				id = "matches3m",
				n = 3,
			},
			{
				closeAfter = nil,
				files = {
					{
						d = 0.23,
						s = "Smoking_strike_matches3f_1",
					},
					{
						d = 0.21,
						s = "Smoking_strike_matches3f_2",
					},
					{
						d = 1.3,
						s = "Smoking_strike_matches3f_3",
					},
				},
				firstOff = 0.67,
				flicks = 3,
				g = nil,
				gaps = {
					0.72,
					0.88,
				},
				id = "matches3f",
				n = 3,
			},
		},
	},
}
